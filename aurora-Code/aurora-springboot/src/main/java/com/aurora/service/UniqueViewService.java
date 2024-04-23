package com.aurora.service;

import com.aurora.model.dto.UniqueViewDTO;
import com.aurora.entity.UniqueView;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UniqueViewService extends IService<UniqueView> {

    /**
    * @Description: 在数据库得到今日之前5天的访问信息量列表
    * @Param: []
    * @return: java.util.List<com.aurora.model.dto.UniqueViewDTO>
    */
    List<UniqueViewDTO> listUniqueViews();

}
