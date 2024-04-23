package com.aurora.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_friend_link")
/**
 * 友链管理的实体类
 */
public class FriendLink {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    private String linkName; // 链接名称

    private String linkAvatar; // 链接头像

    private String linkAddress; // 链接地址

    private String linkIntro; // 链接简介

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
