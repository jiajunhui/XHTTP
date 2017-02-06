package com.kk.taurus.http_helper.callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Taurus on 2017/2/6.
 */

public abstract class ReqCallBack<T> implements HttpCallBack {

    @Override
    public void onFailure(Exception e) {

    }

    public abstract void onResponseBean(T result);

    public Type getType() {
        Type mySuperClass = getClazz().getGenericSuperclass();
        Type type = ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
        return type;
    }

    public Class getClazz() {
        return getClass();
    }

}
