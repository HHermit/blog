package com.aurora.service;

import com.aurora.model.dto.OperationLogDTO;
import com.aurora.entity.OperationLog;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.dto.PageResultDTO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OperationLogService extends IService<OperationLog> {

    /**
     * 查询操作日志列表
     */
    PageResultDTO<OperationLogDTO> listOperationLogs(ConditionVO conditionVO);

}
