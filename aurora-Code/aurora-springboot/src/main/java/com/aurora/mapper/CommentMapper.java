package com.aurora.mapper;

import com.aurora.model.dto.CommentAdminDTO;
import com.aurora.model.dto.CommentCountDTO;
import com.aurora.model.dto.CommentDTO;
import com.aurora.model.dto.ReplyDTO;
import com.aurora.entity.Comment;
import com.aurora.model.vo.CommentVO;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 分页 查询评论列表
     * @param commentVO 评论查询条件
     * @return 返回评论DTO列表
     */
    List<CommentDTO> listComments(@Param("current") Long current, @Param("size") Long size, @Param("commentVO") CommentVO commentVO);

    /**
     * 根据评论ID列表查询回复列表
     * @param commentIdList 评论ID列表
     * @return 返回回复DTO列表
     */
    List<ReplyDTO> listReplies(@Param("commentIds") List<Integer> commentIdList);

    /**
     * 查询前六条热门评论
     * @return 返回评论DTO列表
     */
    List<CommentDTO> listTopSixComments();

    /**
     * 根据条件统计评论数量
     * @param conditionVO 查询条件
     * @return 返回评论数量
     */
    Integer countComments(@Param("conditionVO") ConditionVO conditionVO);

    /**
     * 管理员视图 分页 查询评论列表
     * @param conditionVO 查询条件
     * @return 返回评论列表
     */
    List<CommentAdminDTO> listCommentsAdmin(@Param("current") Long current, @Param("size") Long size, @Param("conditionVO") ConditionVO conditionVO);

    /**
     * 根据类型 和 主题ID列表 查询评论数量
     * @param type 评论类型
     * @param topicIds 主题ID列表
     * @return 返回评论数量DTO列表
     */
    List<CommentCountDTO> listCommentCountByTypeAndTopicIds(@Param("type") Integer type, @Param("topicIds") List<Integer> topicIds);

    /**
     * 根据类型 和 主题ID 查询评论数量
     * @param type 评论类型
     * @param topicId 主题ID
     * @return 返回单个评论数量DTO
     */
    CommentCountDTO listCommentCountByTypeAndTopicId(@Param("type") Integer type, @Param("topicId") Integer topicId);

}
