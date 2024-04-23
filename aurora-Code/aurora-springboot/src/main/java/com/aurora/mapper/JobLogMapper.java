package com.aurora.mapper;

import com.aurora.entity.JobLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobLogMapper extends BaseMapper<JobLog> {
    
    /**
    * @Description: 返回所有日志分组的组名
    * @Param: []
    * @return: java.util.List<java.lang.String>
    */
    List<String> listJobLogGroups();

}
