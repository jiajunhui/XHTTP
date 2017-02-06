package com.kk.taurus.http_helper.callback;

import okhttp3.Response;

/**
 * Created by Taurus on 2017/2/4.
 */

public interface HttpCallBack {

    int ERROR_TYPE_RESPONSE = 0;
    int ERROR_TYPE_NETWORK = 1;

    void onStart();
    void onSuccess(Response response);
    void onError(int errorType, Response response);
    void onFailure(Exception e);
}
