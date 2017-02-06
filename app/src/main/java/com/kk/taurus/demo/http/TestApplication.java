package com.kk.taurus.demo.http;

import android.app.Application;

import com.kk.taurus.http_helper.XHTTP;

/**
 * Created by Taurus on 2017/2/6.
 */

public class TestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XHTTP.init(this,null);
    }
}
