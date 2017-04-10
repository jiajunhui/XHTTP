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

package com.kk.taurus.http_helper.bean;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Taurus on 2017/2/4.
 */

public abstract class AbsResponse<T> implements Serializable {
    /**
     * HTTP status code, such as 200,404.
     */
    public int code;

    /**
     * HTTP response message
     */
    public String message;

    /**
     * user data
     */
    public T result;

    public Type getType() {
        Type mySuperClass = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
        return type;
    }

}
