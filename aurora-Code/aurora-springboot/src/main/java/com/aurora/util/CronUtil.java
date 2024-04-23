package com.aurora.util;

import org.quartz.CronExpression;

import java.util.Date;
/**
 *  @description:cron表达式操作工具类
 *  
*/
public class CronUtil {

    /**
    * @Description: 检查指定CRON表达式是否合理
    * @Param: [cronExpression]
    * @return: boolean
    */
    public static boolean isValid(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }

    public static String getInvalidMessage(String cronExpression) {
        try {
            new CronExpression(cronExpression);
            return null;
        } catch (Exception pe) {
            return pe.getMessage();
        }
    }

    /**
    * @Description: 根据传入的表达式获取下一次的执行时间
    * @Param: [cronExpression]
    * @return: java.util.Date
    */
    public static Date getNextExecution(String cronExpression) {
        try {
            CronExpression cron = new CronExpression(cronExpression);
            return cron.getNextValidTimeAfter(new Date(System.currentTimeMillis()));
//            System.out.println(cron.getNextValidTimeAfter(new Date()));
//            return cron.getNextInvalidTimeAfter(new Date());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
