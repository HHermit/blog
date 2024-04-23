package com.aurora.mapper;

import com.aurora.model.dto.ResourceRoleDTO;
import com.aurora.model.dto.RoleDTO;
import com.aurora.entity.Role;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 获取资源角色列表。
     *
     * @return 返回资源角色的DTO列表。
     */
    List<ResourceRoleDTO> listResourceRoles();

    /**
     * 根据用户信息ID列出角色。
     *
     * @param userInfoId 用户信息的ID。
     * @return 返回与指定用户信息ID相关联的角色名称列表。
     */
    List<String> listRolesByUserInfoId(@Param("userInfoId") Integer userInfoId);

    /**
     * 分页 查询 角色列表。
     * @return 返回分页后的角色DTO列表。
     */
    List<RoleDTO> listRoles(@Param("current") Long current, @Param("size") Long size, @Param("conditionVO") ConditionVO conditionVO);

}
