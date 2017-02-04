package com.kk.taurus.demo.http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kk.taurus.http_helper.XHTTP;
import com.kk.taurus.http_helper.bean.XRequest;
import com.kk.taurus.http_helper.callback.BeanCallBack;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XRequest xRequest = new XRequest();
        xRequest.setUrl("http://v.juhe.cn/weixin/query");
        xRequest.addParams("pno",1);
        xRequest.addParams("ps",20);
        xRequest.addParams("key","211fa04958b2cc67461c9afc01965db5");

        XHTTP.newGet(xRequest, new BeanCallBack<WxArticleRsp>() {
            @Override
            public void onResponseBean(WxArticleRsp result) {
                System.out.println("data : " + result);
            }
        });

    }
}
