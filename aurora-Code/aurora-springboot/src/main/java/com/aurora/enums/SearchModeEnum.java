package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 项目搜索引擎的相关策略：es和mysql
 */
@Getter
@AllArgsConstructor
public enum SearchModeEnum {

    MYSQL("mysql", "mySqlSearchStrategyImpl"),

    ELASTICSEARCH("elasticsearch", "esSearchStrategyImpl");

    private final String mode;

    private final String strategy;

    public static String getStrategy(String mode) {
        for (SearchModeEnum value : SearchModeEnum.values()) {
            if (value.getMode().equals(mode)) {
                return value.getStrategy();
            }
        }
        return null;
    }

}