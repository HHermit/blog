package com.aurora.service;

import com.aurora.model.dto.CommentAdminDTO;
import com.aurora.model.dto.CommentDTO;
import com.aurora.model.dto.ReplyDTO;
import com.aurora.entity.Comment;
import com.aurora.model.vo.CommentVO;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.vo.ReviewVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CommentService extends IService<Comment> {

    /**
     * 保存评论信息 并发邮件给对应用户，提醒留言
     */
    void saveComment(CommentVO commentVO);

    /**
     * 分页查询评论列表
     * @param commentVO 包含查询条件的评论信息对象
     * @return 返回评论的分页查询结果，包含评论列表和分页信息
     */
    PageResultDTO<CommentDTO> listComments(CommentVO commentVO);

    /**
     * 根据评论ID列出所有回复
     * @param commentId 评论的ID
     */
    List<ReplyDTO> listRepliesByCommentId(Integer commentId);

    /**
     * 列出前六个评论
     * @return 返回评论列表，最多包含六个评论
     */
    List<CommentDTO> listTopSixComments();

    /**
     * 管理员分页查询评论列表
     * @param conditionVO 包含查询条件的管理员查询对象
     * @return 返回管理员视图下的评论分页查询结果，包含评论列表和分页信息
     */
    PageResultDTO<CommentAdminDTO> listCommentsAdmin(ConditionVO conditionVO);

    /**
     * 更新评论的审核状态
     * @param reviewVO 包含审核信息的审核对象
     */
    void updateCommentsReview(ReviewVO reviewVO);

}
