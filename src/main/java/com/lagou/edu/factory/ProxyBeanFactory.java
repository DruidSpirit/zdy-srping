package com.lagou.edu.factory;

import com.lagou.edu.dto.BeanDefinition;
import com.lagou.edu.enums.ProxyType;
import com.lagou.edu.handle.ProxyHandle;
import net.sf.cglib.proxy.Enhancer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProxyBeanFactory {

    private Map<String, BeanDefinition> beanDefinitionList;
    private ProxyType proxyType;

    public ProxyBeanFactory(BeanDefinitionFactory beanDefinitionFactory) {
        this.beanDefinitionList = beanDefinitionFactory.getBeanDefinitionList();
        this.proxyType = beanDefinitionFactory.getProxyType();
    }

    /**
     * 实例化bean
     */
    public void initializeBean( Map<String,Object> beanPool ){

        //  实例化bean
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionList.entrySet()) {
            try {
                Object o = entry.getValue().getBeanClass().newInstance();
                beanPool.put(entry.getKey(),o);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //  bean属性值注入并返回后置执行器容器
        List<String> afterInvokeNames = injectProperty(beanPool);

        // 获取后置执行处理器并且执行，
        for (Map.Entry<String, Object> entry : beanPool.entrySet()) {
            //  如果是后置处理本身则不进行动态代理
            if ( afterInvokeNames.contains(entry.getKey()) ){
                continue;
            }
            //  对bean对象进行动态代理
            for (String afterInvokeName : afterInvokeNames) {
                ProxyHandle proxyHandle = (ProxyHandle) beanPool.get(afterInvokeName);
                Object afterProxy = afterInvokeProxyHandle(entry.getValue(), proxyHandle);
                beanPool.put(entry.getKey(),afterProxy);
            }
        }
    }

    /**
     * bena属性注入
     */
    private List<String> injectProperty(Map<String,Object> beanPool ){

        //  后置执行器容器
        List<String> afterInvokeName = new ArrayList<>();
        for (Map.Entry<String, Object> entry : beanPool.entrySet()) {

            //  获取需要属性注入的方法集合和其对用的BeanDefinition
            BeanDefinition beanDefinition = beanDefinitionList.get(entry.getKey());
            Map<String, String> properties = beanDefinition.getProperties();

            //  beanDefinition.getProperties();集合 key代表注入属性的set方法调用,value代表注入的其他beanId
            for (Map.Entry<String, String> propertiesEntry : properties.entrySet()) {
                //  获取注入需要调用的方法
                Method method = Arrays.stream(beanDefinition.getBeanClass().getDeclaredMethods())
                        .filter(med -> med.getName().equals(propertiesEntry.getKey()))
                        .findFirst().get();
                //  获取注入的参数
                Object param = beanPool.get( propertiesEntry.getValue() );
                try {
                    //  开始注入
                    method.invoke(entry.getValue(), param);
                    //  将后置执行器放入容器中
                    if ( entry.getValue() instanceof ProxyHandle ) {
                        afterInvokeName.add( beanDefinition.getBeanId() );
                    }
                    beanPool.put( beanDefinition.getBeanId(), entry.getValue());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
        return afterInvokeName;
    }

    /**
     * 后置处理器动态代理
     */
    private Object afterInvokeProxyHandle( Object obj, ProxyHandle proxyHandle ){

        proxyHandle.setProxyEd( obj );
        switch (this.proxyType){
            case JDK_PROXY:
                System.out.println("====================使用jdk动态代理==========================");
                return  Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),proxyHandle);
            case CGLIB_PROXY:
                System.out.println("====================使用cglib动态代理==========================");
                return  Enhancer.create(obj.getClass(),proxyHandle);
            default:
                throw new RuntimeException("找不到代理方式");
        }
    }
}
