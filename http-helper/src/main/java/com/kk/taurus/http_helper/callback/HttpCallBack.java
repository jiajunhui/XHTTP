package com.kk.taurus.http_helper.callback;

import com.kk.taurus.http_helper.bean.BaseResponse;

import okhttp3.Response;

/**
 * Created by Taurus on 2017/2/4.
 */

public interface HttpCallBack<T extends BaseResponse> {
    void onSuccess(Response response);
    void onResponseBean(T result);
    void onError(Response response);
    void onFailure(Exception e);
}
