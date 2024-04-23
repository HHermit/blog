package com.aurora.service;


import com.aurora.model.dto.JobLogDTO;
import com.aurora.entity.JobLog;
import com.aurora.model.vo.JobLogSearchVO;
import com.aurora.model.dto.PageResultDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface JobLogService extends IService<JobLog> {

    /**
     * 查询作业日志列表。
     */
    PageResultDTO<JobLogDTO> listJobLogs(JobLogSearchVO jobLogSearchVO);

    /**
     * 批量删除作业日志。
     */
    void deleteJobLogs(List<Integer> ids);

    /**
     * 清理所有作业日志。
     */
    void cleanJobLogs();

    /**
     * 查询所有作业日志分组。
     */
    List<String> listJobLogGroups();

}
