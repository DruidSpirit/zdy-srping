package com.lagou.edu.handle;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Component;
import com.lagou.edu.annotation.Transactional;
import com.lagou.edu.utils.TransactionManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class TransactionManagerHandle extends ProxyHandle{

    @Autowired("transactionManager")
    private TransactionManager transactionManager;

    /**
     * 动态代理回调函数
     *
     * @param proxyEd 被代理对象本身
     * @param method  被代理对象方法
     * @param args    方法传入参数
     * @return 执行方法返回参数
     */
    @Override
    protected Object doProxyHandle(Object proxyEd, Method method, Object[] args) throws Throwable{

        Transactional classAnnotation = proxyEd.getClass().getAnnotation(Transactional.class);
        Transactional methodAnnotation = method.getAnnotation(Transactional.class);

        //  不开启事务执行方法
        if ( classAnnotation == null &&  methodAnnotation == null ) {
                return method.invoke(proxyEd,args);
        }

        //  开启事务执行方法
        Object result = null;
        try {
            // 开启事务(关闭事务的自动提交)
            transactionManager.beginTransaction();
            System.out.println("======================开启事务=====================");
            result = method.invoke(proxyEd, args);
            // 提交事务
            System.out.println("======================事务提交=====================");
            transactionManager.commit();

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            // 回滚事务
            transactionManager.rollback();
            System.out.println("======================回滚事务=====================");
            // 抛出异常便于上层servlet捕获
            throw e;
        }

        return result;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
