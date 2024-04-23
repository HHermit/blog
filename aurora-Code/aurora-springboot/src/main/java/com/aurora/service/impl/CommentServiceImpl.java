package com.aurora.service.impl;

import com.alibaba.fastjson.JSON;
import com.aurora.model.dto.*;
import com.aurora.entity.Article;
import com.aurora.entity.Comment;
import com.aurora.entity.Talk;
import com.aurora.entity.UserInfo;
import com.aurora.enums.CommentTypeEnum;
import com.aurora.exception.BizException;
import com.aurora.mapper.ArticleMapper;
import com.aurora.mapper.CommentMapper;
import com.aurora.mapper.TalkMapper;
import com.aurora.mapper.UserInfoMapper;
import com.aurora.service.AuroraInfoService;
import com.aurora.service.CommentService;
import com.aurora.util.HTMLUtil;
import com.aurora.util.PageUtil;
import com.aurora.util.UserUtil;
import com.aurora.model.vo.CommentVO;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.vo.ReviewVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.aurora.constant.CommonConstant.*;
import static com.aurora.constant.RabbitMQConstant.EMAIL_EXCHANGE;
import static com.aurora.enums.CommentTypeEnum.*;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Value("${website.url}")
    private String websiteUrl;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TalkMapper talkMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private AuroraInfoService auroraInfoService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //存储评论的
    private static final List<Integer> types = new ArrayList<>();

    /**
    * @Description: 通过遍历CommentTypeEnum枚举类的所有值，将每个值的Type添加到types集合中。
    * @Param: []
    * @return: void
    */
    @PostConstruct
    public void init() {
        CommentTypeEnum[] values = CommentTypeEnum.values();
        for (CommentTypeEnum value : values) {
            types.add(value.getType());
        }
    }

    @Override
    public void saveComment(CommentVO commentVO) {
        //校验评论是否合理
        checkCommentVO(commentVO);

        //查看网站设置 是否进行评论审核  从而设置评论的审核选项（需要审核，就设置审核不通过）
        WebsiteConfigDTO websiteConfig = auroraInfoService.getWebsiteConfig();
        Integer isCommentReview = websiteConfig.getIsCommentReview();

        //过滤评论中的html标签
        commentVO.setCommentContent(HTMLUtil.filter(commentVO.getCommentContent()));

        //封装评论数据
        Comment comment = Comment.builder()
                .userId(UserUtil.getUserDetailsDTO().getUserInfoId())
                .replyUserId(commentVO.getReplyUserId())
                .topicId(commentVO.getTopicId())
                .commentContent(commentVO.getCommentContent())
                .parentId(commentVO.getParentId())
                .type(commentVO.getType())
                //根据是否审核设置评论的审核值，不开启审核默认该评论审核通过（true） 否则为（flase）
                .isReview(isCommentReview == TRUE ? FALSE : TRUE)
                .build();

        commentMapper.insert(comment);

        //设置评论人的昵称
        String fromNickname = UserUtil.getUserDetailsDTO().getNickname();

        //如果开启邮箱通知 则异步进行通知
        if (websiteConfig.getIsEmailNotice().equals(TRUE)) {
            CompletableFuture.runAsync(() -> notice(comment, fromNickname));
        }
    }

    @Override
    public PageResultDTO<CommentDTO> listComments(CommentVO commentVO) {
        Integer commentCount = commentMapper.selectCount(new LambdaQueryWrapper<Comment>()
                .eq(Objects.nonNull(commentVO.getTopicId()), Comment::getTopicId, commentVO.getTopicId())
                .eq(Comment::getType, commentVO.getType())
                .isNull(Comment::getParentId)
                .eq(Comment::getIsReview, TRUE));
        if (commentCount == 0) {
            return new PageResultDTO<>();
        }
        List<CommentDTO> commentDTOs = commentMapper.listComments(PageUtil.getLimitCurrent(), PageUtil.getSize(), commentVO);
        if (CollectionUtils.isEmpty(commentDTOs)) {
            return new PageResultDTO<>();
        }
        List<Integer> commentIds = commentDTOs.stream()
                .map(CommentDTO::getId)
                .collect(Collectors.toList());
        List<ReplyDTO> replyDTOS = commentMapper.listReplies(commentIds);
        Map<Integer, List<ReplyDTO>> replyMap = replyDTOS.stream()
                .collect(Collectors.groupingBy(ReplyDTO::getParentId));
        commentDTOs.forEach(item -> item.setReplyDTOs(replyMap.get(item.getId())));
        return new PageResultDTO<>(commentDTOs, commentCount);
    }

    @Override
    public List<ReplyDTO> listRepliesByCommentId(Integer commentId) {
        return commentMapper.listReplies(Collections.singletonList(commentId));
    }

    @Override
    public List<CommentDTO> listTopSixComments() {
        return commentMapper.listTopSixComments();
    }

    @SneakyThrows
    @Override
    public PageResultDTO<CommentAdminDTO> listCommentsAdmin(ConditionVO conditionVO) {
        CompletableFuture<Integer> asyncCount = CompletableFuture.supplyAsync(() -> commentMapper.countComments(conditionVO));
        List<CommentAdminDTO> commentBackDTOList = commentMapper.listCommentsAdmin(PageUtil.getLimitCurrent(), PageUtil.getSize(), conditionVO);
        return new PageResultDTO<>(commentBackDTOList, asyncCount.get());
    }

    @Override
    public void updateCommentsReview(ReviewVO reviewVO) {
        List<Comment> comments = reviewVO.getIds().stream().map(item -> Comment.builder()
                        .id(item)
                        .isReview(reviewVO.getIsReview())
                        .build())
                .collect(Collectors.toList());
        this.updateBatchById(comments);
    }

    /**
    * @Description: 对前端传来的评论信息进行校验 是否合理
    * @Param: [commentVO]
    * @return: void
    */
    public void checkCommentVO(CommentVO commentVO) {
        //判断类型
        if (!types.contains(commentVO.getType())) {
            throw new BizException("参数校验异常");
        }
        //判断是说说还是文章下的评论  主题id就是文章id和说说id
        if (Objects.requireNonNull(getCommentEnum(commentVO.getType())) == ARTICLE || Objects.requireNonNull(getCommentEnum(commentVO.getType())) == TALK) {
            if (Objects.isNull(commentVO.getTopicId())) {
                throw new BizException("参数校验异常");
            } else {
                if (Objects.requireNonNull(getCommentEnum(commentVO.getType())) == ARTICLE) {
                    //根据文章id查询实体
                    Article article = articleMapper.selectOne(new LambdaQueryWrapper<Article>().select(Article::getId, Article::getUserId).eq(Article::getId, commentVO.getTopicId()));
                    if (Objects.isNull(article)) {
                        throw new BizException("参数校验异常");
                    }
                }
                if (Objects.requireNonNull(getCommentEnum(commentVO.getType())) == TALK) {
                    //根据说说id查询实体
                    Talk talk = talkMapper.selectOne(new LambdaQueryWrapper<Talk>().select(Talk::getId, Talk::getUserId).eq(Talk::getId, commentVO.getTopicId()));
                    if (Objects.isNull(talk)) {
                        throw new BizException("参数校验异常");
                    }
                }
            }
        }
        //根据评论是 友链 关于我 留言 哪种类型 进行校验处理
        if (Objects.requireNonNull(getCommentEnum(commentVO.getType())) == LINK
                || Objects.requireNonNull(getCommentEnum(commentVO.getType())) == ABOUT
                || Objects.requireNonNull(getCommentEnum(commentVO.getType())) == MESSAGE) {
            if (Objects.nonNull(commentVO.getTopicId())) {
                throw new BizException("参数校验异常");
            }
        }

        if (Objects.isNull(commentVO.getParentId())) {
            if (Objects.nonNull(commentVO.getReplyUserId())) {
                //父评论为空，但是回复用户不空，不合理
                throw new BizException("参数校验异常");
            }
        }

        if (Objects.nonNull(commentVO.getParentId())) {
            Comment parentComment = commentMapper.selectOne(new LambdaQueryWrapper<Comment>().select(Comment::getId, Comment::getParentId, Comment::getType).eq(Comment::getId, commentVO.getParentId()));
            if (Objects.isNull(parentComment)) {
                throw new BizException("参数校验异常");
            }
            if (Objects.nonNull(parentComment.getParentId())) {
                throw new BizException("参数校验异常");
            }
            if (!commentVO.getType().equals(parentComment.getType())) {
                throw new BizException("参数校验异常");
            }
            if (Objects.isNull(commentVO.getReplyUserId())) {
                throw new BizException("参数校验异常");
            } else {
                UserInfo existUser = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().select(UserInfo::getId).eq(UserInfo::getId, commentVO.getReplyUserId()));
                if (Objects.isNull(existUser)) {
                    throw new BizException("参数校验异常");
                }
            }
        }
    }

    /**
    * @Description: 评论消息通知
    * @Param: [comment, fromNickname：评论人的昵称]
    * @return: void
    */
    private void notice(Comment comment, String fromNickname) {

        //1，子评论 回复用户自己（可能跟父评论是用一人【结束方法】，也可能不是【不结束方法】）
        if (comment.getUserId().equals(comment.getReplyUserId())) {
            if (Objects.nonNull(comment.getParentId())) {
                Comment parentComment = commentMapper.selectById(comment.getParentId());
                //如果父评论的用户id和评论用户id相同   则结束方法
                if (parentComment.getUserId().equals(comment.getUserId())) {
                    return;
                }
            }
        }

        //2.父评论 管理员账号发布 不发送通知，不需要发送通知【管理员和博主同一人】
        if (comment.getUserId().equals(BLOGGER_ID) && Objects.isNull(comment.getParentId())) {
            return;
        }

        //3.子评论
        if (Objects.nonNull(comment.getParentId())) {
            //获取父评论实体
            Comment parentComment = commentMapper.selectById(comment.getParentId());
            //子评论 回复评论下的其他评论用户（不是回复父评论的作者 且不是回复给自己的）
            if (!comment.getReplyUserId().equals(parentComment.getUserId())
                    && !comment.getReplyUserId().equals(comment.getUserId())) {
                //获取回复用户信息实体
                UserInfo userInfo = userInfoMapper.selectById(comment.getUserId());
                //获取被回复用户信息实体
                UserInfo replyUserinfo = userInfoMapper.selectById(comment.getReplyUserId());
                Map<String, Object> map = new HashMap<>();
                String topicId = Objects.nonNull(comment.getTopicId()) ? comment.getTopicId().toString() : "";
                String url = websiteUrl + getCommentPath(comment.getType()) + topicId;
                map.put("content", userInfo.getNickname() + "在" + Objects.requireNonNull(getCommentEnum(comment.getType())).getDesc()
                        + "的评论区@了你，"
                        + "<a style=\"text-decoration:none;color:#12addb\" href=\"" + url + "\">点击查看</a>");
                EmailDTO emailDTO = EmailDTO.builder()
                        .email(replyUserinfo.getEmail())
                        .subject(MENTION_REMIND)
                        .template("common.html")
                        .commentMap(map)
                        .build();
                //转发到rabbit中，由对应的监听器进行发送邮件
                rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
            }

            //子评论和父评论是同一个用户
            if (comment.getUserId().equals(parentComment.getUserId())) {
                return;
            }
        }


        //4.父评论 非管理员账号发布  和  子评论 回复父评论的作责
        //设置通知主题
        String title;
        //收件人id
        Integer userId = BLOGGER_ID;
        //获取评论所在主题id
        String topicId = Objects.nonNull(comment.getTopicId()) ? comment.getTopicId().toString() : "";

        //配置收件人信息
        if (Objects.nonNull(comment.getReplyUserId())) {
            //如果存在被回复人（表明该评论是子（回复）评论） 设置收件人为被回复的用户id
            userId = comment.getReplyUserId();
        } else {
            //如果不存在被回复人（表明该评论是父评论）
            switch (Objects.requireNonNull(getCommentEnum(comment.getType()))) {
                case ARTICLE:
                    //设置文章的作者
                    userId = articleMapper.selectById(comment.getTopicId()).getUserId();
                    break;
                case TALK:
                    //设置说说的作者
                    userId = talkMapper.selectById(comment.getTopicId()).getUserId();
                default:
                    break;
            }
        }

        if (Objects.requireNonNull(getCommentEnum(comment.getType())).equals(ARTICLE)) {
            //如果是文章的父评论 设置主题为文章标题
            title = articleMapper.selectById(comment.getTopicId()).getArticleTitle();
        } else {
            //其他则根据枚举类型描述进行
            title = Objects.requireNonNull(getCommentEnum(comment.getType())).getDesc();
        }

        //获取收件人的邮箱
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (StringUtils.isNotBlank(userInfo.getEmail())) {
            EmailDTO emailDTO = getEmailDTO(comment, userInfo, fromNickname, topicId, title);
            //rabbit队列发送消息，让对应监听器进行处理
            rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
        }
    }

    /**
    * @Description: 封装邮件实体对象DTO
    * @Param: [comment：需要保存的评论实体, userInfo：收件人信息, fromNickname：评论人的昵称, topicId：评论所在主题, title：主题标题], topicId, title]
    * @return: com.aurora.model.dto.EmailDTO
    */
    private EmailDTO getEmailDTO(Comment comment, UserInfo userInfo, String fromNickname, String topicId, String title) {
        EmailDTO emailDTO = new EmailDTO();
        //map 存储邮件的一些内容
        Map<String, Object> map = new HashMap<>();
        //判断评论是否审核过关
        if (comment.getIsReview().equals(TRUE)) {
            //配置对应资源的访问链接
            String url = websiteUrl + getCommentPath(comment.getType()) + topicId;
            //不存在父评论 发送通知给对应资源的作者
            if (Objects.isNull(comment.getParentId())) {
                emailDTO.setEmail(userInfo.getEmail());
                emailDTO.setSubject(COMMENT_REMIND);
                emailDTO.setTemplate("owner.html");
                String createTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(comment.getCreateTime());
                map.put("time", createTime);
                map.put("url", url);
                map.put("title", title);
                map.put("nickname", fromNickname);
                map.put("content", comment.getCommentContent());
            } else {
            //存在父评论 发送通知给父评论的作者
                //获取父评论实体
                Comment parentComment = commentMapper.selectOne(new LambdaQueryWrapper<Comment>().select(Comment::getUserId, Comment::getCommentContent, Comment::getCreateTime).eq(Comment::getId, comment.getParentId()));
                //确保userInfo（被回复）对象中存储的用户信息与parentComment中的用户ID相匹配。
                if (!userInfo.getId().equals(parentComment.getUserId())) {
                    userInfo = userInfoMapper.selectById(parentComment.getUserId());
                }
                emailDTO.setEmail(userInfo.getEmail());
                emailDTO.setSubject(COMMENT_REMIND);
                emailDTO.setTemplate("user.html");
                map.put("url", url);
                map.put("title", title);
                String createTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(parentComment.getCreateTime());
                map.put("time", createTime);
                map.put("toUser", userInfo.getNickname());
                map.put("fromUser", fromNickname);
                map.put("parentComment", parentComment.getCommentContent());
                // 判断被回复的用户ID是否与父评论的用户ID是否相同
                if (!comment.getReplyUserId().equals(parentComment.getUserId())) {
                    //如果被回复人的id和父评论的id不相等 同notice方法中的3逻辑，但是此处的收件人是父评论人
                    // 根据被回复用户的ID查询用户信息
                    UserInfo mentionUserInfo = userInfoMapper.selectById(comment.getReplyUserId());
                    // 如果查询到的用户有网站信息，则在评论中添加超链接
                    if (Objects.nonNull(mentionUserInfo.getWebsite())) {
                        map.put("replyComment", "<a style=\"text-decoration:none;color:#12addb\" href=\""
                                + mentionUserInfo.getWebsite()
                                + "\">@" + mentionUserInfo.getNickname() + " " + "</a>" + parentComment.getCommentContent());
                    } else {
                        // 如果没有网站信息，只显示昵称
                        map.put("replyComment", "@" + mentionUserInfo.getNickname() + " " + parentComment.getCommentContent());
                    }
                } else {
                    // 如果被回复的用户ID与父评论的用户ID相同，直接显示评论内容
                    map.put("replyComment", comment.getCommentContent());
                }

            }
        } else {
        //评论未审核通过 发送邮件给管理员处理
            String adminEmail = userInfoMapper.selectById(BLOGGER_ID).getEmail();
            emailDTO.setEmail(adminEmail);
            emailDTO.setSubject(CHECK_REMIND);
            emailDTO.setTemplate("common.html");
            map.put("content", "您收到了一条新的回复，请前往后台管理页面审核");
        }
        emailDTO.setCommentMap(map);
        return emailDTO;
    }

}
