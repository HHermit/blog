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
@TableName("t_user_auth")
/**
 * 用户认证信息类，用于存储用户认证相关的数据。
 */
public class UserAuth {

    /**
     * 主键ID，采用数据库自增策略
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联到用户信息表的用户信息ID
     */
    private Integer userInfoId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码，通常进行加密存储
     */
    private String password;

    /**
     * 登录类型，邮箱登录（1）和QQ登录（qq互联未申请成功）
     */
    private Integer loginType;

    /**
     * 用户登录时的IP地址
     */
    private String ipAddress;

    /**
     * IP地址来源，例如：中国河南省，河北省等
     */
    private String ipSource;

    /**
     * 记录创建时间，插入数据库时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 记录更新时间，更新数据库时自动填充
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 最后一次登录的时间记录
     */
    private LocalDateTime lastLoginTime;
}

