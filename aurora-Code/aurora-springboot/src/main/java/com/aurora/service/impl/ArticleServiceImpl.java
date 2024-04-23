package com.aurora.service.impl;

import com.alibaba.fastjson.JSON;
import com.aurora.model.dto.*;
import com.aurora.entity.Article;
import com.aurora.entity.ArticleTag;
import com.aurora.entity.Category;
import com.aurora.entity.Tag;
import com.aurora.enums.FileExtEnum;
import com.aurora.enums.FilePathEnum;
import com.aurora.exception.BizException;
import com.aurora.mapper.ArticleMapper;
import com.aurora.mapper.ArticleTagMapper;
import com.aurora.mapper.CategoryMapper;
import com.aurora.mapper.TagMapper;
import com.aurora.service.ArticleService;
import com.aurora.service.ArticleTagService;
import com.aurora.service.RedisService;
import com.aurora.service.TagService;
import com.aurora.strategy.context.SearchStrategyContext;
import com.aurora.strategy.context.UploadStrategyContext;
import com.aurora.util.BeanCopyUtil;
import com.aurora.util.PageUtil;
import com.aurora.util.UserUtil;
import com.aurora.model.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.aurora.constant.RabbitMQConstant.SUBSCRIBE_EXCHANGE;
import static com.aurora.constant.RedisConstant.*;
import static com.aurora.enums.ArticleStatusEnum.*;
import static com.aurora.enums.StatusCodeEnum.ARTICLE_ACCESS_FAIL;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UploadStrategyContext uploadStrategyContext;

    @Autowired
    private SearchStrategyContext searchStrategyContext;

    @SneakyThrows
    @Override
    public TopAndFeaturedArticlesDTO listTopAndFeaturedArticles() {
        List<ArticleCardDTO> articleCardDTOs = articleMapper.listTopAndFeaturedArticles();

        if (articleCardDTOs.size() == 0) {
            return new TopAndFeaturedArticlesDTO();
        } else if (articleCardDTOs.size() > 3) {
            //截取列表前三个元素 从索引0开始，到索引3（不包含）结束
            articleCardDTOs = articleCardDTOs.subList(0, 3);
        }

        TopAndFeaturedArticlesDTO topAndFeaturedArticlesDTO = new TopAndFeaturedArticlesDTO();
        topAndFeaturedArticlesDTO.setTopArticle(articleCardDTOs.get(0));
        articleCardDTOs.remove(0);
        topAndFeaturedArticlesDTO.setFeaturedArticles(articleCardDTOs);
        return topAndFeaturedArticlesDTO;
    }

    @SneakyThrows
    @Override
    public PageResultDTO<ArticleCardDTO> listArticles() {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDelete, 0)
                .in(Article::getStatus, 1, 2);

        /*
            发起一个异步任务去执行数据库查询以获取文章数量，不阻塞当前线程，可以和后边的mapper查询一起进行，提高效率
            后续可以在将来某个时间点通过调用get()等方法来获取异步计算的结果
         */
        CompletableFuture<Integer> asyncCount = CompletableFuture.supplyAsync(() -> articleMapper.selectCount(queryWrapper));
        List<ArticleCardDTO> articles = articleMapper.listArticles(PageUtil.getLimitCurrent(), PageUtil.getSize());
        return new PageResultDTO<>(articles, asyncCount.get());
    }

    @SneakyThrows
    @Override
    public PageResultDTO<ArticleCardDTO> listArticlesByCategoryId(Integer categoryId) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<Article>().eq(Article::getCategoryId, categoryId);
        CompletableFuture<Integer> asyncCount = CompletableFuture.supplyAsync(() -> articleMapper.selectCount(queryWrapper));
        List<ArticleCardDTO> articles = articleMapper.getArticlesByCategoryId(PageUtil.getLimitCurrent(), PageUtil.getSize(), categoryId);
        return new PageResultDTO<>(articles, asyncCount.get());
    }

    @SneakyThrows
    @Override
    public ArticleDTO getArticleById(Integer articleId) {
        Article articleForCheck = articleMapper.selectOne(new LambdaQueryWrapper<Article>().eq(Article::getId, articleId));
        if (Objects.isNull(articleForCheck)) {
            return null;
        }
        //判断文章状态，看用户是否有查看这个文章的权限
        if (articleForCheck.getStatus().equals(2)) {
            Boolean isAccess;
            try {
                //在redis中查找 当前用户和访问文章 对应key value是否存在，存在表示当前用户有该文章的权限
                isAccess = redisService.sIsMember(ARTICLE_ACCESS + UserUtil.getUserDetailsDTO().getId(), articleId);
            } catch (Exception exception) {
                throw new BizException(ARTICLE_ACCESS_FAIL);
            }
            if (isAccess.equals(false)) {
                throw new BizException(ARTICLE_ACCESS_FAIL);
            }
        }
        //更新文章的访问人数
        updateArticleViewsCount(articleId);
        //异步查询文章
        CompletableFuture<ArticleDTO> asyncArticle = CompletableFuture.supplyAsync(() -> articleMapper.getArticleById(articleId));
        //异步查询上一篇文章card
        CompletableFuture<ArticleCardDTO> asyncPreArticle = CompletableFuture.supplyAsync(() -> {
            ArticleCardDTO preArticle = articleMapper.getPreArticleById(articleId);
            //如果没有前一篇文章，则选择最后一篇文章，构成循环。
            if (Objects.isNull(preArticle)) {
                preArticle = articleMapper.getLastArticle();
            }
            return preArticle;
        });
        //异步查询下一篇文章card
        CompletableFuture<ArticleCardDTO> asyncNextArticle = CompletableFuture.supplyAsync(() -> {
            ArticleCardDTO nextArticle = articleMapper.getNextArticleById(articleId);
            if (Objects.isNull(nextArticle)) {
                nextArticle = articleMapper.getFirstArticle();
            }
            return nextArticle;
        });
        //获得异步中的数据（文章详细）
        ArticleDTO article = asyncArticle.get();

        if (Objects.isNull(article)) {
            return null;
        }
        //获取对应键值对的分数（访问量）
        Double score = redisService.zScore(ARTICLE_VIEWS_COUNT, articleId);

        if (Objects.nonNull(score)) {
            article.setViewCount(score.intValue());
        }

        article.setPreArticleCard(asyncPreArticle.get());
        article.setNextArticleCard(asyncNextArticle.get());
        
        return article;
    }

    @Override
    public void accessArticle(ArticlePasswordVO articlePasswordVO) {
        Article article = articleMapper.selectOne(new LambdaQueryWrapper<Article>().eq(Article::getId, articlePasswordVO.getArticleId()));

        if (Objects.isNull(article)) {
            throw new BizException("文章不存在");
        }
        //校验文章访问密码
        if (article.getPassword().equals(articlePasswordVO.getArticlePassword())) {
            //密码正确，则设置一对key（关联用户id）和value（文章id），存入redis中set数据类型，表示用户具有这个文章的访问权限
            redisService.sAdd(ARTICLE_ACCESS + UserUtil.getUserDetailsDTO().getId(), articlePasswordVO.getArticleId());
        } else {
            throw new BizException("密码错误");
        }
    }

    @SneakyThrows
    @Override
    public PageResultDTO<ArticleCardDTO> listArticlesByTagId(Integer tagId) {
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, tagId);
        //统计文章数量，用来渲染页面
        CompletableFuture<Integer> asyncCount = CompletableFuture.supplyAsync(() -> articleTagMapper.selectCount(queryWrapper));
        //SQl执行
        List<ArticleCardDTO> articles = articleMapper.listArticlesByTagId(PageUtil.getLimitCurrent(), PageUtil.getSize(), tagId);
        return new PageResultDTO<>(articles, asyncCount.get());
    }

    @SneakyThrows
    @Override
    public PageResultDTO<ArchiveDTO> listArchives() {
        //获取文章数量
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<Article>().eq(Article::getIsDelete, 0).eq(Article::getStatus, 1);
        CompletableFuture<Integer> asyncCount = CompletableFuture.supplyAsync(() -> articleMapper.selectCount(queryWrapper));
        //归档文章sql执行
        List<ArticleCardDTO> articles = articleMapper.listArchives(PageUtil.getLimitCurrent(), PageUtil.getSize());
        //将查询出来的文章列表 List<ArticleCardDTO> 按照年月进行分组 存入List<ArchiveDTO>
        HashMap<String, List<ArticleCardDTO>> map = new HashMap<>();
        for (ArticleCardDTO article : articles) {
            LocalDateTime createTime = article.getCreateTime();
            int month = createTime.getMonth().getValue();
            int year = createTime.getYear();
            String key = year + "-" + month;
            if (Objects.isNull(map.get(key))) {
                List<ArticleCardDTO> articleCardDTOS = new ArrayList<>();
                articleCardDTOS.add(article);
                map.put(key, articleCardDTOS);
            } else {
                map.get(key).add(article);
            }
        }
        List<ArchiveDTO> archiveDTOs = new ArrayList<>();
        map.forEach((key, value) -> archiveDTOs.add(ArchiveDTO.builder().Time(key).articles(value).build()));
        //按时间降序进行排序
        archiveDTOs.sort((o1, o2) -> {
            String[] o1s = o1.getTime().split("-");
            String[] o2s = o2.getTime().split("-");
            int o1Year = Integer.parseInt(o1s[0]);
            int o1Month = Integer.parseInt(o1s[1]);
            int o2Year = Integer.parseInt(o2s[0]);
            int o2Month = Integer.parseInt(o2s[1]);
            if (o1Year > o2Year) {
                //01 放在 02前边
                return -1;
            } else if (o1Year < o2Year) {
                //01 放在 02后边
                return 1;
            } else return Integer.compare(o2Month, o1Month);
        });
        return new PageResultDTO<>(archiveDTOs, asyncCount.get());
    }

    @SneakyThrows
    @Override
    public PageResultDTO<ArticleAdminDTO> listArticlesAdmin(ConditionVO conditionVO) {
        //获取文章数量
        CompletableFuture<Integer> asyncCount = CompletableFuture.supplyAsync(() -> articleMapper.countArticleAdmins(conditionVO));
        //获取管理员视图的文章信息
        List<ArticleAdminDTO> articleAdminDTOs = articleMapper.listArticlesAdmin(PageUtil.getLimitCurrent(), PageUtil.getSize(), conditionVO);
        //获取存储在redis中的文章访问量的有序集合
        Map<Object, Double> viewsCountMap = redisService.zAllScore(ARTICLE_VIEWS_COUNT);
        //通过文章id给对应DTO中设置文章访问量
        articleAdminDTOs.forEach(item -> {
            Double viewsCount = viewsCountMap.get(item.getId());
            if (Objects.nonNull(viewsCount)) {
                item.setViewsCount(viewsCount.intValue());
            }
        });
        return new PageResultDTO<>(articleAdminDTOs, asyncCount.get());
    }

    @Override
    //@Transactional 注解表示，抛出任何异常都回滚
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateArticle(ArticleVO articleVO) {
        //存储文章分类
        Category category = saveArticleCategory(articleVO);

        Article article = BeanCopyUtil.copyObject(articleVO, Article.class);
        if (Objects.nonNull(category)) {
            article.setCategoryId(category.getId());
        }
        article.setUserId(UserUtil.getUserDetailsDTO().getUserInfoId());
        //调用mp中封装的方法
        this.saveOrUpdate(article);
        //存储文章所关联的标签
        saveArticleTag(articleVO, article.getId());
        if (article.getStatus().equals(1)) {
            //将文章ID转换为JSON字节数组，并将其封装在Message对象中，然后发送到指定的交换器SUBSCRIBE_EXCHANGE，
            //路由键为"*"：表示转发到所有绑定该交换器的队列
            //该交换的目的是发送邮件通知订阅博客的用户，有新文章更新
            rabbitTemplate.convertAndSend(SUBSCRIBE_EXCHANGE, "*", new Message(JSON.toJSONBytes(article.getId()), new MessageProperties()));
        }
    }

    @Override
    public void updateArticleTopAndFeatured(ArticleTopFeaturedVO articleTopFeaturedVO) {
        Article article = Article.builder()
                .id(articleTopFeaturedVO.getId())
                .isTop(articleTopFeaturedVO.getIsTop())
                .isFeatured(articleTopFeaturedVO.getIsFeatured())
                .build();
        articleMapper.updateById(article);
    }

    @Override
    public void updateArticleDelete(DeleteVO deleteVO) {
        List<Article> articles = deleteVO.getIds().stream()
                .map(id -> Article.builder()
                        .id(id)
                        .isDelete(deleteVO.getIsDelete())
                        .build())
                .collect(Collectors.toList());
        this.updateBatchById(articles);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticles(List<Integer> articleIds) {
        //删除文章id对应的tag标签之间的联系
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>()
                .in(ArticleTag::getArticleId, articleIds));
        //批量删除文章
        articleMapper.deleteBatchIds(articleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleAdminViewDTO getArticleByIdAdmin(Integer articleId) {
        Article article = articleMapper.selectById(articleId);
        Category category = categoryMapper.selectById(article.getCategoryId());
        String categoryName = null;
        if (Objects.nonNull(category)) {
            categoryName = category.getCategoryName();
        }
        List<String> tagNames = tagMapper.listTagNamesByArticleId(articleId);
        ArticleAdminViewDTO articleAdminViewDTO = BeanCopyUtil.copyObject(article, ArticleAdminViewDTO.class);
        articleAdminViewDTO.setCategoryName(categoryName);
        articleAdminViewDTO.setTagNames(tagNames);
        return articleAdminViewDTO;
    }

    @Override
    public List<String> exportArticles(List<Integer> articleIds) {
        List<Article> articles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .select(Article::getArticleTitle, Article::getArticleContent)
                .in(Article::getId, articleIds));
        List<String> urls = new ArrayList<>();
        for (Article article : articles) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(article.getArticleContent().getBytes())) {
                //上传文章到对应的文件服务器（OSS或者MinIO）,返回对应url访问路径
                String url = uploadStrategyContext.executeUploadStrategy(article.getArticleTitle() + FileExtEnum.MD.getExtName(), inputStream, FilePathEnum.MD.getPath());
                urls.add(url);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BizException("导出文章失败");
            }
        }
        return urls;
    }

    @Override
    public List<ArticleSearchDTO> listArticlesBySearch(ConditionVO condition) {
        return searchStrategyContext.executeSearchStrategy(condition.getKeywords());
    }

    /**
    * @Description: 更新文章访问量+1 ARTICLE_VIEWS_COUNT：redis中有序集合名  articleId：集合的value值  1：集合的自增score值
    * @Param: [articleId]
    * @return: void
    */
    public void updateArticleViewsCount(Integer articleId) {
        redisService.zIncr(ARTICLE_VIEWS_COUNT, articleId, 1D);
    }

    /**
    * @Description: 保存文章时，如果不存在对应文章分类，则新建一个文章分类
    * @Param: [articleVO]
    * @return: com.aurora.entity.Category
    */
    private Category saveArticleCategory(ArticleVO articleVO) {
        //查询分类是否存在
        Category category = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getCategoryName, articleVO.getCategoryName()));
        //如果 查询结果为空 且 传入的articleVO对象的状态是草稿，则创建一个新的Category对象，存入到数据库
        if (Objects.isNull(category) && !articleVO.getStatus().equals(DRAFT.getStatus())) {
            category = Category.builder()
                    .categoryName(articleVO.getCategoryName())
                    .build();
            categoryMapper.insert(category);
        }
        return category;
    }

    /**
    * @Description: 保存文章对应的标签
    * @Param: [articleVO, articleId]
    * @return: void
    */
    @Transactional(rollbackFor = Exception.class)
    public void saveArticleTag(ArticleVO articleVO, Integer articleId) {
        //先删除文章原先关联的所有标签
        if (Objects.nonNull(articleVO.getId())) {
            articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>()
                    .eq(ArticleTag::getArticleId, articleVO.getId()));
        }
        //获取文章新关联的标签列表
        List<String> tagNames = articleVO.getTagNames();
        if (CollectionUtils.isNotEmpty(tagNames)) {
            //查询数据库中已经存在的标签
            List<Tag> existTags = tagService.list(new LambdaQueryWrapper<Tag>()
                    .in(Tag::getTagName, tagNames));
            //获取已存在的标签名集合
            List<String> existTagNames = existTags.stream()
                    .map(Tag::getTagName)
                    .collect(Collectors.toList());
            //获取已存在的标签id集合
            List<Integer> existTagIds = existTags.stream()
                    .map(Tag::getId)
                    .collect(Collectors.toList());
            //删除文章新关联的标签列表中已经存在的标签
            tagNames.removeAll(existTagNames);
            //将剩余不存在的添加到数据库各个表中
            if (CollectionUtils.isNotEmpty(tagNames)) {
                List<Tag> tags = tagNames.stream().map(item -> Tag.builder()
                                .tagName(item)
                                .build())
                        .collect(Collectors.toList());
                tagService.saveBatch(tags);
                List<Integer> tagIds = tags.stream()
                        .map(Tag::getId)
                        .collect(Collectors.toList());
                existTagIds.addAll(tagIds);
            }
            //将文章和标签的对应关系天年到数据库中
            List<ArticleTag> articleTags = existTagIds.stream().map(item -> ArticleTag.builder()
                            .articleId(articleId)
                            .tagId(item)
                            .build())
                    .collect(Collectors.toList());
            articleTagService.saveBatch(articleTags);
        }
    }

}
