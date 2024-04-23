package com.aurora.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_menu")
/**
 * 菜单类
 * 用于描述系统的菜单项，包括菜单的各个属性。
 */
public class Menu {

    /**
    * 主键ID，自增长
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单路径
     */
    private String path;

    /**
     * 菜单对应的组件，主要就是前端中的Vue组件
     */
    private String component;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单排序号
     */
    private Integer orderNum;

    /**
     * 父菜单ID
     */
    private Integer parentId;

    /**
     * 是否隐藏，0为不隐藏，1为隐藏
     */
    private Integer isHidden;



    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}

