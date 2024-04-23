package com.aurora.constant;

public interface ScheduleConstant {

    /**
     * 默认处理，返回原始对象
     */
    int MISFIRE_DEFAULT = 0;

    /**
     * 立刻执行
     */
    int MISFIRE_IGNORE_MISFIRES = 1;

    /**
     * 执行一次
     */
    int MISFIRE_FIRE_AND_PROCEED = 2;

    /**
     * 放弃执行
     */
    int MISFIRE_DO_NOTHING = 3;

    /**
     * 作为 jobKey和triggerKey 的name前缀名
     */
    String TASK_CLASS_NAME = "TASK_CLASS_NAME";

    /**
     * JobDataMap的key
     */
    String TASK_PROPERTIES = "TASK_PROPERTIES";

}
