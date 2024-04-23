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
@TableName("t_talk")
/**
 * 说说实体类，映射页面的说说管理
 */
public class Talk {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 用户ID
    private Integer userId;

    // 内容文本
    private String content;

    // 图片地址
    private String images;

    // 是否置顶，1表示置顶，0表示不置顶
    private Integer isTop;

    // 状态 1.公开 2.私密
    private Integer status;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}