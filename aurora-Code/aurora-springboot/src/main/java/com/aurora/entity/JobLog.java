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
@TableName("t_job_log")
/**
 * 定时任务的日志
 */
public class JobLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer jobId;

    private String jobName;

    private String jobGroup;

    /**
     * 调用目标字符串
     */
    private String invokeTarget;

    private String jobMessage;

    /**
     * 执行状态（1正常 0失败）
     */
    private Integer status;

    private String exceptionInfo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private Date startTime;

    private Date endTime;
}
