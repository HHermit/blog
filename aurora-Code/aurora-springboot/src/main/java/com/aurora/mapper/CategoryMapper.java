package com.aurora.mapper;

import com.aurora.model.dto.CategoryAdminDTO;
import com.aurora.model.dto.CategoryDTO;
import com.aurora.entity.Category;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 获取所有分类的信息列表。
     * @return 返回一个包含所有分类信息的DTO列表。
     */
    List<CategoryDTO> listCategories();

    /**
     * 获取管理员视图下的分类信息列表。
     *
     * @param conditionVO 查询条件对象，包含各种用于筛选分类的条件。
     * @return 返回一个包含管理员视图下分类信息的DTO列表。
     */
    List<CategoryAdminDTO> listCategoriesAdmin(@Param("current") Long current, @Param("size") Long size, @Param("conditionVO") ConditionVO conditionVO);
}
