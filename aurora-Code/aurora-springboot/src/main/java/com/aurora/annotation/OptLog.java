package com.aurora.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OptLog {

    /**
     *  optType 指定操作的类型，为空时表示不指定具体类型，默认为空字符串。
     *  可以根据实际需要设置不同的操作类型，以便在日志记录中进行区分。
     */
    String optType() default "";
}
