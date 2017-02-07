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
