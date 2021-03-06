/*
 * Copyright 2017 jiajunhui
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.kk.taurus.http_helper.callback;

import com.kk.taurus.http_helper.bean.XResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Taurus on 2017/2/6.
 */

public abstract class ReqCallBack<T> implements HttpCallBack {

    private Class<T> response;

    public ReqCallBack(){
        try {
            Type genType = getClass().getGenericSuperclass();
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if(params!=null && params.length>0){
                response = (Class) params[0];
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public abstract void onResponseBean(T result);

    public abstract T parseBean(XResponse response);

    public Type getType() {
        try {
            Type mySuperClass = getClass().getGenericSuperclass();
            Type type = ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
            return type;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public T getResponseInstance(){
        try {
            return response.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
