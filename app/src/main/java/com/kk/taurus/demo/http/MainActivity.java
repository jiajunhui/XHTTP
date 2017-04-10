package com.kk.taurus.demo.http;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kk.taurus.demo.http.bean.NewsResponse;
import com.kk.taurus.http_helper.XHTTP;
import com.kk.taurus.http_helper.bean.XRequest;
import com.kk.taurus.http_helper.callback.BeanCallBack;
import com.kk.taurus.http_helper.callback.OnDownloadCallBack;
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
    private TextView mTvState;
    private DownloadTask downloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvSpeed = (TextView) findViewById(R.id.tv_speed);
        mTvFileInfo = (TextView) findViewById(R.id.tv_file_info);
        mTvState = (TextView) findViewById(R.id.tv_state);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mTvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(downloadTask==null){
                    startDownload();
                    mTvState.setText("pause");
                }else{
                    if(!downloadTask.isCancel()){
                        downloadTask.cancel();
                        mTvState.setText("start");
                    }else{
                        startDownload();
                        mTvState.setText("pause");
                    }
                }
            }
        });

        XRequest test = new XRequest();
        test.setUrl("http://bike.sjwyb.com/news.json");
        XHTTP.newGet(test, new BeanCallBack<NewsResponse>() {
            @Override
            public void onResponseBean(NewsResponse result) {
                System.out.println("data : " + result);
            }
        });

        XRequest xRequest = new XRequest();
        xRequest.setUrl("http://v.juhe.cn/weixin/query");
        xRequest.addParams("pno",1);
        xRequest.addParams("ps",20);
        xRequest.addParams("key","211fa04958b2cc67461c9afc01965db5");

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
        });

    }

    private void startDownload() {
        String dir = getExternalCacheDir().getAbsolutePath();
        //                System.out.println("download_Test_Progress : curr = " + curr + " total = " + total);
        downloadTask = DownloadManager.download(
                "http://172.16.218.64:8080/lvyexianzong.mkv"
                , dir
                , "test_download.mp4", new OnDownloadCallBack() {
            @Override
            public void onStart() {
                System.out.println("download_Test_Start_Download");
            }

            @Override
            public void onProgress(long curr, long total) {
                mProgressBar.setMax((int) total);
                mProgressBar.setProgress((int) curr);
                mTvFileInfo.setText(BytesHelper.formatBytes(curr) + "/" + BytesHelper.formatBytes(total) + "    " + (curr*100/total) + "%");
            }

            @Override
            public void onSpeed(long byteEverySecond, long bytes, long dms) {
                String s = BytesHelper.formatBytes(byteEverySecond);
                mTvSpeed.setText(s + "/s");
                System.out.println("download_Test_Speed : speed = " + s);
            }

            @Override
            public void onFinish(File file) {
                mTvState.setText("finish");
                mTvState.setTextColor(Color.parseColor("#cccccc"));
                mTvState.setEnabled(false);
                System.out.println("download_Test_Finish_Download " + "fileName = " + file.getName() + " file_size = " + BytesHelper.formatBytes(file.length()));
            }

            @Override
            public void onError(int errorType, Response response) {
                System.out.println("download_Test_OnError : " + errorType + " response : " + (response==null?"null":response.message()));
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
