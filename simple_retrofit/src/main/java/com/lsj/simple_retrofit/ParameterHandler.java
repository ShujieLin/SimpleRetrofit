package com.lsj.simple_retrofit;

/**
 * @description:
 * @date: 2022/3/30
 * @author: linshujie
 */
public abstract class ParameterHandler {
    abstract void apply(ServiceMethod serviceMethod, String value);


    public static class FiledParameterHandler extends ParameterHandler{
        String key;
        public FiledParameterHandler(String key) {
            this.key = key;
        }

        @Override
        void apply(ServiceMethod serviceMethod, String value) {
            serviceMethod.addFileParameter(key,value);
        }
    }
}
