package com.aurora.service;

import com.aurora.model.dto.LabelOptionDTO;
import com.aurora.model.dto.ResourceDTO;
import com.aurora.entity.Resource;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.ResourceVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ResourceService extends IService<Resource> {

    /**
     * 导入资源信息到数据库表中
     * 导入Swagger信息。该方法用于从Swagger源数据中导入相关资源信息，具体实现细节根据实际业务逻辑而定。
     */
    void importSwagger();

    /**
     * 保存或更新资源。
     */
    void saveOrUpdateResource(ResourceVO resourceVO);

    /**
     * 删除资源。根据资源ID，删除相应的资源信息。
     */
    void deleteResource(Integer resourceId);

    /**
     * 列出资源。根据条件VO对象，查询并列出符合条件的资源信息。
     */
    List<ResourceDTO> listResources(ConditionVO conditionVO);

    /**
     * 列出资源选项列表，用于展示对应的选项框
     */
    List<LabelOptionDTO> listResourceOption();

}
