package com.lagou.edu.handle;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 动态代理处理类(采用模版方法模式)
 */
public abstract class ProxyHandle implements InvocationHandler, MethodInterceptor {

    private Object proxyEd;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return doProxyHandle(this.proxyEd,method,args);
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return doProxyHandle(this.proxyEd,method,objects);
    }

    /**
     * 动态代理回调函数
     * @param proxyEd   被代理对象本身
     * @param method    被代理对象方法
     * @param args      方法传入参数
     * @return          执行方法返回参数
     */
    protected abstract Object doProxyHandle( Object proxyEd, Method method, Object[] args ) throws Throwable;

    public Object getProxyEd() {
        return proxyEd;
    }

    public void setProxyEd(Object proxyEd) {
        this.proxyEd = proxyEd;
    }
}
