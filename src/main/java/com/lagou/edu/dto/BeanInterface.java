package com.lagou.edu.dto;

public interface BeanInterface {

    /**
     *获取bean实例
     * @param beanId  bean名称
     * @return        bean实例
     */
    Object getBean(String beanId);
}
