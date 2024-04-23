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
@TableName("t_resource")
/**
 * 接口管理实体类，应用于后台管理中的接口管理设置
 */
public class Resource {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 请求的URL路径
     */
    private String url;

    /**
     * 请求方法，例如GET、POST等
     */
    private String requestMethod;

    /**
     * 父级ID，用于表示资源的层级关系
     */
    private Integer parentId;

    /**
     * 是否允许匿名访问，1表示允许，0表示不允许
     */
    private Integer isAnonymous;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
