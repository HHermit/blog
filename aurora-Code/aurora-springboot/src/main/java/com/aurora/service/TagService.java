package com.aurora.service;

import com.aurora.model.dto.TagAdminDTO;
import com.aurora.model.dto.TagDTO;
import com.aurora.entity.Tag;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.vo.TagVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TagService extends IService<Tag> {

    /**
     * 获取所有标签信息
     */
    List<TagDTO> listTags();

    /**
     * 获取前十個标签信息
     */
    List<TagDTO> listTopTenTags();

    /**
     * 管理员视图下分页查询标签信息
     */
    PageResultDTO<TagAdminDTO> listTagsAdmin(ConditionVO conditionVO);

    /**
     * 管理员视图下 设置搜索条件：标签信息
     */
    List<TagAdminDTO> listTagsAdminBySearch(ConditionVO conditionVO);

    /**
     * 保存或更新标签信息
     */
    void saveOrUpdateTag(TagVO tagVO);

    /**
     * 批量删除标签
     */
    void deleteTag(List<Integer> tagIds);

}
