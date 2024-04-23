package com.aurora.annotation;

import java.lang.annotation.*;

/**
 * 访问限制注解，以实现访问频率的控制。
 *
 * @Target(ElementType.METHOD) 指定该注解适用于方法级别。
 * @Retention(RetentionPolicy.RUNTIME) 指定该注解的生命周期为运行时，因此可以在运行时通过反射读取。
 * @Documented 将该注解包含在Javadoc中。使得用户能清楚了解到使用了此注解的方法有访问频率限制的特性。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {

    /**
     * 限制访问的秒数。在指定的秒数内，对该方法的访问将会被限制。
     */
    int seconds();

    /**
     * 在限制时间内允许的最大访问次数。
     */
    int maxCount();
}

