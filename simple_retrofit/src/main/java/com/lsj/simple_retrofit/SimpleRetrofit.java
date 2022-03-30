package com.lsj.simple_retrofit;

import com.lsj.simple_retrofit.api.WeatherAPI;

import java.io.ObjectInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * @description:
 * @date: 2022/3/30
 * @author: linshujie
 */
public class SimpleRetrofit {
    final Call.Factory callFactory;
    final HttpUrl httpUrl;
    public HttpUrl baseUrl;
    private Map<Method,ServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();

    public SimpleRetrofit(Call.Factory callFactory, HttpUrl httpUrl) {
        this.callFactory = callFactory;
        this.httpUrl = httpUrl;
    }

    public <T> T create(final Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ServiceMethod serviceMethod = loadServiceMethod(method);
                return serviceMethod.invoke(args);
            }
        });
    }

    /**
     *
     * @param method
     * @return
     */
    private ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod result = serviceMethodCache.get(method);
        if (result != null)
            return result;
        synchronized (serviceMethodCache){
            result = serviceMethodCache.get(method);
            if (result == null){
                result = new ServiceMethod.Builder(this,method).build();
                serviceMethodCache.put(method,result);
            }
        }
        return result;
    }

    /**
     * 构建者模式
     */
    public static class Builder {
        private HttpUrl baseUrl;
        private Call.Factory callFactory;

        public Builder callFactory(Call.Factory factory){
            this.callFactory = factory;
            return this;
        }

        public Builder baseUrl(String baseUrl){
            this.baseUrl = HttpUrl.get(baseUrl);
            return this;
        }

        public SimpleRetrofit build(){
            if (baseUrl == null)
                throw new IllegalStateException("Base URL required");
            Call.Factory callFactory = this.callFactory;
            if (callFactory == null)
                callFactory = (Call.Factory) new OkHttpClient();
            return new SimpleRetrofit(callFactory,baseUrl);
        }
    }
}
