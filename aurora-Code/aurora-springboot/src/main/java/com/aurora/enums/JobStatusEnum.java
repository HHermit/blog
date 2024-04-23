package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 定时任务的状态，1为开启，0关闭
 */
@Getter
@AllArgsConstructor
public enum JobStatusEnum {

    NORMAL(1),

    PAUSE(0);

    private final Integer value;

}

