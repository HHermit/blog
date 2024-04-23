package com.aurora.quartz;

import com.aurora.entity.Job;
import com.aurora.util.JobInvokeUtil;
import org.quartz.JobExecutionContext;

/**
 *  @description: 默认的 job实现类，默认：允许并发执行
 */
public class QuartzJobExecution extends AbstractQuartzJob {

    @Override
    protected void doExecute(JobExecutionContext context, Job job) throws Exception {
        JobInvokeUtil.invokeMethod(job);
    }
}
