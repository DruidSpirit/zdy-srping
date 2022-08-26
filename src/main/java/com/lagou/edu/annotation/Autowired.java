package com.lagou.edu.annotation;

import java.lang.annotation.*;

/**
 * 属性注入注解
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    /**
     * 注入bean的名称
     * @return
     */
    String value();
}
