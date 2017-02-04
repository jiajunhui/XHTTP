package com.kk.taurus.http_helper.callback;

import com.kk.taurus.http_helper.bean.BaseResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created by Taurus on 2017/2/4.
 */

public abstract class BeanCallBack<T extends BaseResponse> implements HttpCallBack<T> {

    @Override
    public void onSuccess(Response response) {

    }

    @Override
    public void onError(Response response) {

    }

    @Override
    public void onFailure(Exception e) {

    }

    public Type getType() {
        Type mySuperClass = getClazz().getGenericSuperclass();
        Type type = ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
        return type;
    }

    public Class getClazz() {
        return getClass();
    }

}
