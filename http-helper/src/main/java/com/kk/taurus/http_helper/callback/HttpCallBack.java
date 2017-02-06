package com.kk.taurus.http_helper.callback;

import okhttp3.Response;

/**
 * Created by Taurus on 2017/2/4.
 */

public interface HttpCallBack {
    void onSuccess(Response response);
    void onError(Response response);
    void onFailure(Exception e);
}
