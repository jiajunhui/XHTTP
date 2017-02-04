package com.kk.taurus.http_helper.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by Taurus on 2017/2/4.
 */

public class XRequest implements Serializable {

    private String url;
    private Map<String,Object> mHeaders = new HashMap<>();
    private Map<String,Object> mParams = new HashMap<>();
    private RequestBody requestBody;

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

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }
}
