package com.aurora.util;

import com.aurora.constant.ScheduleConstant;
import com.aurora.entity.Job;
import com.aurora.enums.JobStatusEnum;
import com.aurora.exception.TaskException;
import com.aurora.quartz.QuartzDisallowConcurrentExecution;
import com.aurora.quartz.QuartzJobExecution;
import org.quartz.*;

/**
 *  @description:Quarz调度器工具类，提供了一系列用于创建和管理Quartz调度任务的方法。
 *  
*/
public class ScheduleUtil {

    /**
     * 根据任务的并发设置获取具体的Quartz执行任务类。
     *
     * @param job 任务对象
     * @return Quartz任务类，如果任务支持并发执行则返回QuartzJobExecution类，否则返回QuartzDisallowConcurrentExecution类。
     */
    private static Class<? extends org.quartz.Job> getQuartzJobClass(Job job) {
        boolean isConcurrent = Integer.valueOf(1).equals(job.getConcurrent());
        return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
    }

    /**
     * 获取触发器的键。
     *
     * @param jobId 任务ID
     * @param jobGroup 任务组名
     * @return 触发器键
     */
    public static TriggerKey getTriggerKey(Integer jobId, String jobGroup) {
        return TriggerKey.triggerKey(ScheduleConstant.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 获取任务的键。
     *
     * @param jobId 任务ID
     * @param jobGroup 任务组名
     * @return 任务键
     */
    public static JobKey getJobKey(Integer jobId, String jobGroup) {
        return JobKey.jobKey(ScheduleConstant.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 创建调度任务。
     *
     * @param scheduler 调度器
     * @param job 任务对象
     * @throws SchedulerException 调度异常
     * @throws TaskException 任务异常
     */
    public static void createScheduleJob(Scheduler scheduler, Job job) throws SchedulerException, TaskException {
        //获取具体的Quartz任务类
        Class<? extends org.quartz.Job> jobClass = getQuartzJobClass(job);

        Integer jobId = job.getId();
        String jobGroup = job.getJobGroup();

        // 构建JobDetail实例，定义了job配置，包含引用的具体是哪一个Job（通过jobClass）
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(jobId, jobGroup)).build();

        // 根据任务的CRON表达式构建触发器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder);
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId, jobGroup))
                .withSchedule(cronScheduleBuilder).build();

        // 将任务属性放入任务数据映射中
        jobDetail.getJobDataMap().put(ScheduleConstant.TASK_PROPERTIES, job);

        // 如果任务已存在，则先删除后重新创建
        if (scheduler.checkExists(getJobKey(jobId, jobGroup))) {
            scheduler.deleteJob(getJobKey(jobId, jobGroup));
        }

        //将作业和触发器注册到调度器中，以便在指定的时间或间隔内执行作业。
        scheduler.scheduleJob(jobDetail, trigger);

        // 如果任务状态为暂停，则暂停任务
        if (job.getStatus().equals(JobStatusEnum.PAUSE.getValue())) {
            scheduler.pauseJob(ScheduleUtil.getJobKey(jobId, jobGroup));
        }

    }

    /**
     * 处理触发器的错失触发政策。即因为某种原因导致触发器未正常触发时，触发器需要被重新触发执行，此时需要处理错失触发。
     *
     * @param job 任务对象
     * @param cb 触发器构建器
     * @return 配置了错失触发政策的触发器构建器
     * @throws TaskException 任务异常
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(Job job, CronScheduleBuilder cb)
            throws TaskException {
        switch (job.getMisfirePolicy()) {
            case ScheduleConstant.MISFIRE_DEFAULT:
                // 默认策略：采用触发器的默认策略
                return cb;
            case ScheduleConstant.MISFIRE_IGNORE_MISFIRES:
                // 触发一次执行
                return cb.withMisfireHandlingInstructionIgnoreMisfires();
            case ScheduleConstant.MISFIRE_FIRE_AND_PROCEED:
                //立即触发执行的策略
                return cb.withMisfireHandlingInstructionFireAndProceed();
            case ScheduleConstant.MISFIRE_DO_NOTHING:
                //不做任何处理的策略
                return cb.withMisfireHandlingInstructionDoNothing();
            default:
                throw new TaskException("The task misfire policy '" + job.getMisfirePolicy()
                        + "' cannot be used in cron schedule tasks", TaskException.Code.CONFIG_ERROR);
        }
    }
}

