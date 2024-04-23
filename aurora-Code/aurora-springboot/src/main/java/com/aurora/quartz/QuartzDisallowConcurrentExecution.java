package com.aurora.quartz;

import com.aurora.entity.Job;
import com.aurora.util.JobInvokeUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

/**
 *  @description: 不允许并发执行的Job实现类，
 *  DisallowConcurrentExecution注解： 禁止并发执行多个相同定义的JobDetail,
 *  这个注解是加在Job类上的, 但意思并不是不能同时执行多个Job,
 *  而是不能并发执行同一个Job Definition(由JobDetail定义), 但是可以同时执行多个不同的JobDetail。
*/
@DisallowConcurrentExecution
public class QuartzDisallowConcurrentExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(JobExecutionContext context, Job job) throws Exception {
        JobInvokeUtil.invokeMethod(job);
    }
}
