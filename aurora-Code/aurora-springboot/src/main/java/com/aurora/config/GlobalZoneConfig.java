package com.aurora.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

import static com.aurora.enums.ZoneEnum.SHANGHAI;

/**
 * @author 33477
 * @Description: 全局时区配置类
 */
@Configuration
public class GlobalZoneConfig {

    /**
     * PostConstruct注解：注解表示该方法在  类实例化后执行一次。
     * 刚好会在Configuration注解读取之后实例化，再执行一次方法
     * 获取上海时区的TimeZone对象，并将其设置为系统默认时区。
     */
    @PostConstruct
    public void setGlobalZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(SHANGHAI.getZone()));
    }

}
