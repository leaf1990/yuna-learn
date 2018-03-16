package com.yuna.redis.util;

import javassist.util.proxy.ProxyFactory;

public class JavassistJedisProxyFactory<T> {
    private T target;

    public void setTarget(T target) {
        this.target = target;
    }

    public T getProxy() throws InstantiationException, IllegalAccessException {
        // 代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        // 设置需要创建子类的父类
        proxyFactory.setSuperclass(target.getClass());
        // 定义一个拦截器。在调用目标方法时，Javassist会回调MethodHandler接口方法拦截，
        proxyFactory.setHandler((self, thismethod, proceed, args) -> {
            System.out.println("--------------------------------");
            System.out.println(self.getClass());
            System.out.println("要调用的方法名：" + thismethod.getName());
            System.out.println(proceed.getName());

            Object result = proceed.invoke(self, args);
            //Object result = thismethod.invoke(target, args);
            return result;
        });

        return (T) proxyFactory.createClass().newInstance();
    }
}