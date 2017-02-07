package com.kk.taurus.demo.http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kk.taurus.http_helper.XHTTP;
import com.kk.taurus.http_helper.bean.XRequest;
import com.kk.taurus.http_helper.callback.BeanCallBack;
import com.kk.taurus.http_helper.callback.OnDownloadListener;
import com.kk.taurus.http_helper.download.DownloadManager;
import com.kk.taurus.http_helper.download.DownloadTask;
import com.kk.taurus.http_helper.utils.BytesHelper;

import java.io.File;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private TextView mTvSpeed;
    private TextView mTvFileInfo;
    private DownloadTask downloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvSpeed = (TextView) findViewById(R.id.tv_speed);
        mTvFileInfo = (TextView) findViewById(R.id.tv_file_info);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mTvSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadTask.cancel();
            }
        });

        XRequest xRequest = new XRequest();
        xRequest.setUrl("http://v.juhe.cn/weixin/query");
        xRequest.addParams("pno",1);
        xRequest.addParams("ps",20);
        xRequest.addParams("key","xxx");

        XHTTP.newPost(xRequest, new BeanCallBack<WxArticleRsp>() {
            @Override
            public void onResponseBean(WxArticleRsp result) {
                Toast.makeText(MainActivity.this, "response", Toast.LENGTH_SHORT).show();
                System.out.println("data : " + result);
            }

            @Override
            public void onError(int errorType,Response response) {
                super.onError(errorType,response);
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                super.onFailure(e);
                Toast.makeText(MainActivity.this, "failure", Toast.LENGTH_SHORT).show();
            }
        });

        String dir = getExternalCacheDir().getAbsolutePath();
        //                System.out.println("download_Test_Progress : curr = " + curr + " total = " + total);
        downloadTask = DownloadManager.download(
                "http://172.16.218.64:8080/batamu.mp4"
                , dir
                , null, new OnDownloadListener() {
            @Override
            public void onStart() {
                System.out.println("download_Test_Start_Download");
            }

            @Override
            public void onProgress(long curr, long total) {
                mProgressBar.setMax((int) total);
                mProgressBar.setProgress((int) curr);
                mTvFileInfo.setText(BytesHelper.formatBytes(curr) + "/" + BytesHelper.formatBytes(total));
            }

            @Override
            public void onSpeed(long byteEverySecond, long bytes, long dms) {
                String s = BytesHelper.formatBytes(byteEverySecond);
                mTvSpeed.setText(s + "/s");
                System.out.println("download_Test_Speed : speed = " + s);
            }

            @Override
            public void onFinish(File file) {
                System.out.println("download_Test_Finish_Download");
            }

            @Override
            public void onError(int errorType, Response response) {
                System.out.println("download_Test_OnError : " + errorType + " response : " + (response==null?"null":response.message()));
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("download_Test_onFailure......");
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(downloadTask!=null){
            downloadTask.cancel();
        }
    }
}
