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

package com.kk.taurus.http_helper.download;

import android.text.TextUtils;

import com.kk.taurus.http_helper.bean.XRequest;

/**
 * Created by Taurus on 2017/2/6.
 */

public class DownloadRequest extends XRequest {

    public static final int MAX_REDIRECT_NUM = 3;

    private int redirectCount;

    private String desDir;
    private String rename;

    private ConfigManager.DownloadConfig downloadConfig;

    public int getRedirectCount() {
        return redirectCount;
    }

    public void setRedirectCount(int redirectCount) {
        this.redirectCount = redirectCount;
    }

    public String getDesDir() {
        return desDir;
    }

    public void setDesDir(String desDir) {
        this.desDir = desDir;
    }

    public String getRename() {
        if(TextUtils.isEmpty(rename)){
            return subFileNameByUrl();
        }
        return rename;
    }

    public void setRename(String rename) {
        this.rename = rename;
    }

    public ConfigManager.DownloadConfig getDownloadConfig() {
        return downloadConfig;
    }

    public void setDownloadConfig(ConfigManager.DownloadConfig downloadConfig) {
        this.downloadConfig = downloadConfig;
    }

    private String subFileNameByUrl(){
        int index = getUrl().lastIndexOf("/");
        if(index!=-1){
            int len = getUrl().length();
            return getUrl().substring(index + 1 , len);
        }
        return String.valueOf(System.currentTimeMillis());
    }
}
