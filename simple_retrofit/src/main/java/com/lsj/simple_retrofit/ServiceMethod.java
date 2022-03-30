package com.lsj.simple_retrofit;

import android.content.ContentProviderOperation;

import com.lsj.simple_retrofit.annotation.GET;
import com.lsj.simple_retrofit.annotation.Query;
import com.lsj.simple_retrofit.utils.Logger;

import java.io.OptionalDataException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.attribute.AclEntry;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * @description:
 * @date: 2022/3/30
 * @author: linshujie
 */
public class ServiceMethod {

    private final boolean hasBody;
    private HttpUrl.Builder urlBuilder;
    private HttpUrl baseUrl;
    private String relativeUrl;
    private ParameterHandler[] parameterHandlers;
    private FormBody.Builder formBuil;
    private String httpMethod;
    private Call.Factory callFactory;

    public ServiceMethod(Builder builer) {
        baseUrl = builer.simpleRetrofit.baseUrl;
        callFactory = builer.simpleRetrofit.callFactory;

        httpMethod = builer.httpMethod;
        relativeUrl = builer.relativeUrl;
        hasBody = builer.hasBody;
        parameterHandlers = builer.parameterHandlers;
        if (hasBody){
            formBuil = new FormBody.Builder();
        }
    }

    public Object invoke(Object[] args) {
        HttpUrl url;
        //最终地址
        if (urlBuilder == null){
            urlBuilder = baseUrl.newBuilder(relativeUrl);
        }

        //处理请求地址和参数
        for (int i = 0; i < parameterHandlers.length; i++) {
            ParameterHandler parameterHandler = parameterHandlers[i];
            parameterHandler.apply(this,args[i].toString());
        }
        url = urlBuilder.build();

        //请求体
        FormBody formBody = null;
        if (formBody != null)
            formBody = formBuil.build();

        Request request = new Request.Builder().url(url).method(httpMethod, formBody).build();
        return callFactory.newCall(request);
    }

    public void addFileParameter(String key, String value) {
        formBuil.add(key,value);
    }

    /**
     * 构建者，合并SimpleRetrofit + 通过反射获取的API接口上的全部注解参数信息
     */
    public static class Builder {
        public SimpleRetrofit simpleRetrofit;
        private Annotation[] methodAnnotations;
        private String httpMethod;
        private String relativeUrl;
        private boolean hasBody;
        private Annotation[][] parameterAnnotations;
        private ParameterHandler[] parameterHandlers;

        public Builder(SimpleRetrofit simpleRetrofit, Method method) {
            this.simpleRetrofit = simpleRetrofit;
            //获取方法上的注解
            methodAnnotations = method.getAnnotations();
            //获取方法参数的所有注解
            parameterAnnotations = method.getParameterAnnotations();
        }

        public ServiceMethod build() {
            //处理api方法上的注解
            for (Annotation methodAnnotation : methodAnnotations) {
                if (methodAnnotation instanceof GET){
                    httpMethod = "GET";
                    relativeUrl = ((GET) methodAnnotation).value();
                    hasBody = false;
                    Logger.d("httpMethod = " + httpMethod + " relativeUrl = " + relativeUrl);
                }
                // TODO: 2022/3/30 get 或者其他
            }

            int length = parameterAnnotations.length;
            parameterHandlers = new ParameterHandler[length];
            for (int i = 0; i < length; i++) {
                Annotation[] annotations = parameterAnnotations[i];
                for (Annotation annotation:
                     annotations) {
                    //处理Query注解
                    if (annotation instanceof Query){
                        String key = ((Query) annotation).value();
                        parameterHandlers[i] = new ParameterHandler.FiledParameterHandler(key);
                    }
                }
            }

            return new ServiceMethod(this);
        }

    }
}
