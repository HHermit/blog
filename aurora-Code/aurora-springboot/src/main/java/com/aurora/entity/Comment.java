package com.aurora.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_comment")
public class Comment {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    // 用户id
    private Integer userId;

    // 该评论所回复的用户id
    private Integer replyUserId;

    // 主题id
    private Integer topicId;

    // 评论内容
    private String commentContent;

    // 父级评论id，用于构建评论层级关系
    private Integer parentId;

    // 评论类型 1.文章 2.留言 3.关于我 4.友链 5.说说
    private Integer type;

    // 是否删除，1代表已删除，0代表未删除
    private Integer isDelete;

    // 是否审核通过，1代表已审核通过，0代表未审核
    private Integer isReview;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
