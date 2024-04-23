package com.aurora.service;

import com.aurora.model.dto.CategoryAdminDTO;
import com.aurora.model.dto.CategoryDTO;
import com.aurora.model.dto.CategoryOptionDTO;
import com.aurora.entity.Category;
import com.aurora.model.vo.CategoryVO;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.dto.PageResultDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 获取所有分类信息的列表。
     */
    List<CategoryDTO> listCategories();

    /**
     * 获取管理员视图下的分类信息分页结果。
     */
    PageResultDTO<CategoryAdminDTO> listCategoriesAdmin(ConditionVO conditionVO);

    /**
     * 根据搜索条件获取分类选项列表。
     */
    List<CategoryOptionDTO> listCategoriesBySearch(ConditionVO conditionVO);

    /**
     * 批量删除分类。
     * 分类下存在文章时，不允许删除。
     */
    void deleteCategories(List<Integer> categoryIds);

    /**
     * 保存或更新分类信息。
     */
    void saveOrUpdateCategory(CategoryVO categoryVO);

}
