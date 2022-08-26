package com.lagou.edu.annotation;

import java.lang.annotation.*;

/**
 * 开启事物注解
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transactional {

}
