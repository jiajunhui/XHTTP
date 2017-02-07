package com.kk.taurus.http_helper.callback;

import java.io.File;

import okhttp3.Response;

/**
 * Created by Taurus on 2017/2/6.
 */

public interface OnDownloadListener {

    int ERROR_TYPE_RESPONSE = 0;
    int ERROR_TYPE_NETWORK = 1;

    void onStart();
    void onProgress(long curr, long total);
    void onSpeed(long byteEverySecond, long bytes, long dms);
    void onFinish(File file);
    void onError(int errorType, Response response);
    void onFailure(Exception e);
}
