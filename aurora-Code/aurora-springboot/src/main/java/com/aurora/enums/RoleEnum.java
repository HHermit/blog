package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 账户权限类型：1管理员，2用户，3测试
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    ADMIN(1, "管理员", "admin"),

    USER(2, "用户", "user"),

    TEST(3, "测试", "test");

    private final Integer roleId;

    private final String name;

    private final String label;

}
