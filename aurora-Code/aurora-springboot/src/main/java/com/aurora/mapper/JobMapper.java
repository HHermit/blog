package com.aurora.mapper;

import com.aurora.model.dto.JobDTO;
import com.aurora.entity.Job;
import com.aurora.model.vo.JobSearchVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobMapper extends BaseMapper<Job> {

    /**
     * 统计符合搜索条件的定时任务数量。
     */
    Integer countJobs(@Param("jobSearchVO") JobSearchVO jobSearchVO);

    /**
     * 分页查询 符合搜索条件的 定时任务列表。
     */
    List<JobDTO> listJobs(@Param("current") Long current, @Param("size") Long size, @Param("jobSearchVO")JobSearchVO jobSearchVO);

    /**
     * 查询 所有任务 组的 列表。
     */
    List<String> listJobGroups();

}
