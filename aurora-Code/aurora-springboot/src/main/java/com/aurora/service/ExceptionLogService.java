package com.aurora.service;

import com.aurora.model.dto.ExceptionLogDTO;
import com.aurora.entity.ExceptionLog;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.dto.PageResultDTO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ExceptionLogService extends IService<ExceptionLog> {

    /**
     * 查询异常日志列表
     *
     * @param conditionVO 条件
     */
    PageResultDTO<ExceptionLogDTO> listExceptionLogs(ConditionVO conditionVO);

}
