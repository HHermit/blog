package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录的类型：1邮箱，2QQ
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {

    EMAIL(1, "邮箱登录", ""),

    QQ(2, "QQ登录", "qqLoginStrategyImpl");

    private final Integer type;

    private final String desc;

    private final String strategy;

}
