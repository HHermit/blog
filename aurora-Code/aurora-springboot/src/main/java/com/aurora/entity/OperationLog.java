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
@TableName("t_operation_log")
public class OperationLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 操作的模块
     */
    private String optModule;

    /**
     * 操作对应的URI
     */
    private String optUri;

    /**
     * 操作类型
     */
    private String optType;

    /**
     * 操作方法
     */
    private String optMethod;

    /**
     * 操作描述
     */
    private String optDesc;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 操作进行时的请求参数
     */
    private String requestParam;

    /**
     * 响应数据
     */
    private String responseData;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户IP地址
     */
    private String ipAddress;

    /**
     * IP来源
     */
    private String ipSource;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
