package com.lagou.edu.annotation;

import java.lang.annotation.*;

/**
 * bean扫描注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

    /**
     * 扫描bean的名称
     * @return
     */
    String value() default "";
}
