package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 说说状态：1公开2私密
 */
@Getter
@AllArgsConstructor
public enum TalkStatusEnum {

    PUBLIC(1, "公开"),

    SECRET(2, "私密");

    private final Integer status;

    private final String desc;

}
