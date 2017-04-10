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

import com.kk.taurus.http_helper.bean.AbsResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Taurus on 2017/2/6.
 */

public abstract class ReqCallBack<T extends AbsResponse> implements HttpCallBack {

    private Class<T> response;

    public ReqCallBack(){
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        response = (Class) params[0];
    }

    public abstract void onResponseBean(T result);

    public T getResponseInstance(){
        try {
            return response.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
