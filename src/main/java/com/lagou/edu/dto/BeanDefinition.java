package com.lagou.edu.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * 搜集bean解析元素的实体
 */
public class BeanDefinition {

    /**
     * bean定义的名称
     */
    private String beanId;
    /**
     * bean反射类
     */
    private Class<?> beanClass;
    /**
     * bean注入的属性值
     */
    private Map<String,String> properties = new HashMap<>();

    public BeanDefinition propertyPut( String methodName, String injectBeanId ){
        this.properties.put( methodName, injectBeanId );
        return this;
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
