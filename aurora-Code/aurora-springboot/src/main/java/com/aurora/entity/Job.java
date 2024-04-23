package com.aurora.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_job")
/**
 * 定时任务类
 */
public class Job {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    // 作业名称
    private String jobName;

    // 作业组名
    private String jobGroup;

    // 调用目标，指定作业执行的具体目标，例如调用的类或方法
    private String invokeTarget;

    // 作业的cron表达式，用于定义作业的执行时间
    private String cronExpression;

    // 误火策略，定义作业在误火时应该如何处理
    private Integer misfirePolicy;

    // 是否允许并发执行，1表示允许，0表示禁止
    private Integer concurrent;

    // 作业状态，0表示停止，1表示启动
    private Integer status;

    // 创建时间，自动填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 更新时间，自动填充
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    // 作业备注信息
    private String remark;

    // 下次有效执行时间，该字段不存在于数据库表中
    @TableField(exist = false)
    private Date nextValidTime;


}
