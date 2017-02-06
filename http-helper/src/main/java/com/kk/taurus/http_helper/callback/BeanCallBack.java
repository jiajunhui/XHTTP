package com.kk.taurus.http_helper.callback;

import com.kk.taurus.http_helper.bean.BaseResponse;

import okhttp3.Response;

/**
 * Created by Taurus on 2017/2/4.
 */

public abstract class BeanCallBack<T extends BaseResponse> extends ReqCallBack<T> {

    @Override
    public void onStart() {

    }

    @Override
    public void onSuccess(Response response) {

    }

    @Override
    public void onError(int errorType, Response response) {

    }

    @Override
    public void onFailure(Exception e) {

    }

}
