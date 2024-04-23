package com.aurora.service;

import com.aurora.model.dto.JobDTO;
import com.aurora.entity.Job;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface JobService extends IService<Job> {

    /**
     * 保存作业信息，并将该作业加入调度器管理。
     */
    void saveJob(JobVO jobVO);

    /**
     * 更新作业信息。
     */
    void updateJob(JobVO jobVO);

    /**
     * 根据ID列表批量删除作业。
     * 同时删除调度器中的任务，保证后序不再触发。
     */
    void deleteJobs(List<Integer> tagIds);

    /**
     * 根据作业ID获取作业详情。
     */
    JobDTO getJobById(Integer jobId);

    /**
     * 分页查询作业列表。
     */
    PageResultDTO<JobDTO> listJobs(JobSearchVO jobSearchVO);

    /**
     * 更新作业状态。（暂停/恢复）
     */
    void updateJobStatus(JobStatusVO jobStatusVO);

    /**
     * 执行作业。
     */
    void runJob(JobRunVO jobRunVO);

    /**
     * 列出所有作业组。
     */
    List<String> listJobGroups();

}
