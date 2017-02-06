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
        int len = getUrl().length();
        return getUrl().substring(index + 1 , len);
    }
}
