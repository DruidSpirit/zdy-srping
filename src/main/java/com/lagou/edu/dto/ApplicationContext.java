package com.lagou.edu.dto;

import com.lagou.edu.factory.BeanDefinitionFactory;
import com.lagou.edu.factory.ProxyBeanFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ApplicationContext implements BeanInterface{

    private String xmlPath;

    /**
     * 单例池（为了控制注入顺序用TreeMap,其实可以用hashMap实现，但时间有限就挑简单的来）
     */
    private static Map<String,Object> beanPool = new TreeMap<>();

    public ApplicationContext(String xmlPath) {
        this.xmlPath = xmlPath;
        this.init();
    }

    /**
     * 执行初始化方法
     */
    private void init(){

        //  读取配置文件生成bean工厂
        BeanDefinitionFactory beanDefinitionFactory = new BeanDefinitionFactory(xmlPath);

        //  实例化bean,注入bean属性值，设置动态代理执行后置处理器以此实现aop事务
        ProxyBeanFactory proxyBeanFactory = new ProxyBeanFactory(beanDefinitionFactory);
        proxyBeanFactory.initializeBean( beanPool );

    }

    /**
     * 获取bean实例
     *
     * @param beanId bean名称
     * @return bean实例
     */
    @Override
    public Object getBean(String beanId) {
        return beanPool.get(beanId);
    }
}
