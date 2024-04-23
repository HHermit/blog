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
@TableName(value = "t_photo")
/**
 * 照片实体类
 */
public class Photo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 相册ID
    private Integer albumId;

    // 照片名称
    private String photoName;

    // 照片描述
    private String photoDesc;

    // 照片源文件地址
    private String photoSrc;

    // 标记是否已删除，1表示已删除，0表示未删除
    private Integer isDelete;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}