package com.aurora.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_article")
public class Article {

    // value 值对应的是`t_article`表中字段的名
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    // 用户ID
    private Integer userId;

    // 文章分类ID
    private Integer categoryId;

    // 文章缩略图
    private String articleCover;

    // 文章标题
    private String articleTitle;

    // 文章摘要
    private String articleAbstract;

    // 文章正文内容
    private String articleContent;

    // 文章是否置顶，1表示置顶，0表示不置顶
    private Integer isTop;

    // 文章是否作为特色文章，1表示是，0表示否
    private Integer isFeatured;

    // 文章是否被删除，1表示删除，0表示未删除
    private Integer isDelete;

    // 状态值 1公开 2私密 3草稿
    private Integer status;

    /**
     * 文章类型 1原创 2转载 3翻译
     */
    private Integer type;

    // 文章访问密码，用于保护文章
    private String password;

    // 原始文章链接
    private String originalUrl;

    // 创建时间，在插入数据时自动填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 更新时间，在更新数据时自动填充
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;


}
