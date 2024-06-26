package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 时区：默认东八时区
 */
@Getter
@AllArgsConstructor
public enum ZoneEnum {

    SHANGHAI("Asia/Shanghai", "中国上海");


    private final String zone;


    private final String desc;
}
