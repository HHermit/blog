package com.aurora.service;

import com.aurora.model.dto.RoleDTO;
import com.aurora.model.dto.UserRoleDTO;
import com.aurora.entity.Role;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.vo.RoleVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RoleService extends IService<Role> {

    /**
     * 获取角色列表
     */
    List<UserRoleDTO> listUserRoles();

    /**
     * 分页查询角色信息 包含角色对应的菜单权限和资源权限
    */
    PageResultDTO<RoleDTO> listRoles(ConditionVO conditionVO);

    /**
     * 保存或更新角色信息 （菜单权限，资源权限）
     * 刷新Security上下文中的权限角色信息
     */
    void saveOrUpdateRole(RoleVO roleVO);

    /**
     * 批量删除角色
     */
    void deleteRoles(List<Integer> ids);

}
