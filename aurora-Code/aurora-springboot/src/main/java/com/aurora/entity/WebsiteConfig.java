package com.aurora.entity;

import com.baomidou.mybatisplus.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "t_website_config")
/**
 * 网站配置的相关实体类
 */
public class WebsiteConfig {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 相关配置在前段设置之后，通过JSON格式存储到mysql数据库中
    private String config;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}