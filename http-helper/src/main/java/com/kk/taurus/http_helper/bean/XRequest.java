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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Taurus on 2017/2/4.
 */

public class XRequest implements Serializable {

    private String url;
    private Map<String,Object> mHeaders = new HashMap<>();
    private Map<String,Object> mParams = new HashMap<>();

    public XRequest addHeader(String key, Object value){
        mHeaders.put(key, value);
        return this;
    }

    public XRequest addParams(String key, Object value){
        mParams.put(key, value);
        return this;
    }

    public Map<String, Object> getHeaders() {
        return mHeaders;
    }

    public Map<String, Object> getParams() {
        return mParams;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
