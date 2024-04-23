package com.aurora.quartz;

import com.aurora.constant.ScheduleConstant;
import com.aurora.entity.Job;
import com.aurora.entity.JobLog;
import com.aurora.mapper.JobLogMapper;
import com.aurora.util.ExceptionUtil;
import com.aurora.util.SpringUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.Date;

import static com.aurora.constant.CommonConstant.ONE;
import static com.aurora.constant.CommonConstant.ZERO;

/**
 *  @description:  Quartz定义 抽象任务类  用于调度任务，自定义实现doExecute方法 主要就是定义job的具体任务内容
 *  
*/
public abstract class AbstractQuartzJob implements org.quartz.Job {

    /**
     * 初始化了一个静态、不可变的日志记录器对象，用于在 AbstractQuartzJob 类中记录日志信息。
     * 等同于添加注解@Slf4j，加入后，便可以在该类代码中使用 log.info()、log.error() 等方法。
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractQuartzJob.class);

    /**
     * 定义了一个私有的静态常量 THREAD_LOCAL，用于在多线程环境下保存任务执行的时间信息。
     * 通过 ThreadLocal 实现，确保每个线程都有自己的副本，互不影响。
     */
    private static final ThreadLocal<Date> THREAD_LOCAL = new ThreadLocal<>();

    /**
    * @Description:
    * @Param: [context] JobExecutionContext 对象中保存着该 job 运行时的一些信息 一般就是调度器调用job传递来的。可以跟ScheduleUtil联系理解。
    * @return: void
    */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        //定义job实体类，接收上下文中的Job相关数据
        Job job = new Job();
        BeanUtils.copyProperties(context.getMergedJobDataMap().get(ScheduleConstant.TASK_PROPERTIES), job);

        //逻辑封装
        try {
            before(context, job);
            doExecute(context, job);
            after(context, job, null);
        } catch (Exception e) {
            log.error("任务执行异常:", e);
            after(context, job, e);
        }
    }

    /**
    * @Description: 用于记录任务开始时间
    * @Param: [context, job]
    * @return: void
    */
    protected void before(JobExecutionContext context, Job job) {
        THREAD_LOCAL.set(new Date());
    }

    /**
    * @Description: 用于记录定时任务运行的日志，并保存到数据库中
    * @Param: [context, job, e]
    * @return: void
    */
    protected void after(JobExecutionContext context, Job job, Exception e) {
        Date startTime = THREAD_LOCAL.get();
        THREAD_LOCAL.remove();

        final JobLog jobLog = new JobLog();
        jobLog.setJobId(job.getId());
        jobLog.setJobName(job.getJobName());
        jobLog.setJobGroup(job.getJobGroup());
        jobLog.setInvokeTarget(job.getInvokeTarget());
        jobLog.setStartTime(startTime);
        jobLog.setEndTime(new Date());

        long runMs = jobLog.getEndTime().getTime() - jobLog.getStartTime().getTime();
        jobLog.setJobMessage(jobLog.getJobName() + " 总共耗时：" + runMs + "毫秒");

        if (e != null) {
            jobLog.setStatus(ZERO);
            jobLog.setExceptionInfo(ExceptionUtil.getTrace(e));
        } else {
            jobLog.setStatus(ONE);
        }
        //调用容器中的JobLogMapper对象，将jobLog对象插入到数据库中
        SpringUtil.getBean(JobLogMapper.class).insert(jobLog);
    }

    /**
    * @Description: 具体的任务执行代码。在子类中实现。
    * @Param: [context, job]
    * @return: void
    */
    protected abstract void doExecute(JobExecutionContext context, Job job) throws Exception;
}
