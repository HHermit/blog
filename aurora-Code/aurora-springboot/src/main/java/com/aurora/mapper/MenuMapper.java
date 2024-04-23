package com.aurora.mapper;

import com.aurora.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据用户id查询对应的后台菜单列表
     *
     * @param userInfoId 用户id
     * @return 菜单列表
     */
    List<Menu> listMenusByUserInfoId(Integer userInfoId);

}
