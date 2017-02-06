package com.kk.taurus.http_helper.download;

import com.kk.taurus.http_helper.bean.XRequest;

/**
 * Created by Taurus on 2017/2/6.
 */

public class DownloadRequest extends XRequest {

    public static final int MAX_REDIRECT_NUM = 3;

    private int redirectCount;

    private String desPath;
    private String rename;

    public int getRedirectCount() {
        return redirectCount;
    }

    public void setRedirectCount(int redirectCount) {
        this.redirectCount = redirectCount;
    }

    public String getDesPath() {
        return desPath;
    }

    public void setDesPath(String desPath) {
        this.desPath = desPath;
    }

    public String getRename() {
        return rename;
    }

    public void setRename(String rename) {
        this.rename = rename;
    }
}
