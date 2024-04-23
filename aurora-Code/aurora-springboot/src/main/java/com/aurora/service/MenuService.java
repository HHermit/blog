package com.aurora.service;

import com.aurora.model.dto.LabelOptionDTO;
import com.aurora.model.dto.MenuDTO;
import com.aurora.model.dto.UserMenuDTO;
import com.aurora.entity.Menu;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.IsHiddenVO;
import com.aurora.model.vo.MenuVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MenuService extends IService<Menu> {

    /**
     * 根据条件查询菜单列表
     */
    List<MenuDTO> listMenus(ConditionVO conditionVO);

    /**
     * 保存或更新菜单信息
     */
    void saveOrUpdateMenu(MenuVO menuVO);

    /**
     * 更新菜单的隐藏状态
     */
    void updateMenuIsHidden(IsHiddenVO isHiddenVO);

    /**
     * 删除指定菜单
     */
    void deleteMenu(Integer menuId);

    /**
     * 查询 某个角色具有的 菜单选项列表
     */
    List<LabelOptionDTO> listMenuOptions();

    /**
     * 查询当前用户所具有的菜单列表，在后台首页侧边栏显示
     */
    List<UserMenuDTO> listUserMenus();

}
