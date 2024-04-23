package com.aurora.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 异常log实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_exception_log")
public class ExceptionLog {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 出现异常的url请求
    private String optUri;

    // 操作方法
    private String optMethod;

    // 请求方法
    private String requestMethod;

    // 请求参数
    private String requestParam;

    // 操作描述
    private String optDesc;

    // 异常信息
    private String exceptionInfo;

    // 请求IP地址
    private String ipAddress;

    // IP来源
    private String ipSource;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
