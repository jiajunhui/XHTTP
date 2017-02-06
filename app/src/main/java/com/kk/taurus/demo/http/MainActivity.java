package com.kk.taurus.demo.http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.kk.taurus.http_helper.XHTTP;
import com.kk.taurus.http_helper.bean.XRequest;
import com.kk.taurus.http_helper.callback.BeanCallBack;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XRequest xRequest = new XRequest();
        xRequest.setUrl("http://v.juhe.cn/weixin/query");
        xRequest.addParams("pno",1);
        xRequest.addParams("ps",20);
        xRequest.addParams("key","xxx");

        XHTTP.newGet(xRequest, new BeanCallBack<WxArticleRsp>() {
            @Override
            public void onResponseBean(WxArticleRsp result) {
                Toast.makeText(MainActivity.this, "response", Toast.LENGTH_SHORT).show();
                System.out.println("data : " + result);
            }

            @Override
            public void onError(Response response) {
                super.onError(response);
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                super.onFailure(e);
                Toast.makeText(MainActivity.this, "failure", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
