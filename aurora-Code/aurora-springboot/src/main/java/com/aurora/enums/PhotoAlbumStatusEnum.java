package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 相册状态的枚举类型：1公开，2私密
 */
@Getter
@AllArgsConstructor
public enum PhotoAlbumStatusEnum {

    PUBLIC(1, "公开"),

    SECRET(2, "私密");

    private final Integer status;

    private final String desc;

}
