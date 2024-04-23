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
@TableName("t_user_info")
/**
 * 用户信息表
 */
public class UserInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    // 邮箱地址
    private String email;

    // 昵称
    private String nickname;

    // 头像URL
    private String avatar;

    // 个人简介
    private String intro;

    // 个人网站或博客地址
    private String website;

    // 是否订阅本博客文章更新通知的标志（1表示订阅，0表示未订阅），订阅的话，有文章发布时，会通过邮件通知订阅者
    private Integer isSubscribe;

    // 是否禁用的标志（1表示禁用，0表示启用）
    private Integer isDisable;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
