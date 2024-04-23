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
@TableName(value = "t_photo_album")
public class PhotoAlbum {

    @TableId(type = IdType.AUTO)
    private Integer id;

    //相册名称
    private String albumName;

    //相册描述
    private String albumDesc;

    //相册封面
    private String albumCover;

    // 标记相册是否被删除，1表示已删除，0表示未删除
    private Integer isDelete;

    // 状态值 1公开 2私密
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}