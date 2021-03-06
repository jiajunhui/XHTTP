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

import com.kk.taurus.http_helper.XHTTP;
import com.kk.taurus.http_helper.callback.OnDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Taurus on 2017/2/6.
 */

public class DownloadThread implements Runnable {

    private Call mCall;
    private DownloadRequest downloadRequest;
    private OnDownloadListener onDownloadListener;
    private boolean mQuit;

    public DownloadThread(Call call, DownloadRequest downloadRequest, OnDownloadListener onDownloadListener){
        this.mCall = call;
        this.downloadRequest = downloadRequest;
        this.onDownloadListener = onDownloadListener;
    }

    @Override
    public void run() {
        if(!mQuit){
            onStart();
            startDownload();
        }
    }

    public void quit() {
        mCall.cancel();
        mQuit = true;
    }

    public boolean isQuit(){
        return mQuit;
    }

    private void startDownload() {
        try {
            Response response = mCall.execute();
            if(response.isRedirect() && downloadRequest.getRedirectCount()< DownloadRequest.MAX_REDIRECT_NUM){
                onRedirect(response);
                mCall.cancel();
            }else if(response.isSuccessful()){
                onInputStream(response);
            }else{
                onError(OnDownloadListener.ERROR_TYPE_RESPONSE,response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            onError(OnDownloadListener.ERROR_TYPE_EXCEPTION,null);
        }
    }

    private void onError(final int errorType, final Response response) {
        if(onDownloadListener!=null){
            XHTTP.handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownloadListener.onError(errorType,response);
                }
            });
        }
        onSpeed(0,1);
    }

    private void onStart() {
        if(onDownloadListener!=null){
            XHTTP.handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownloadListener.onStart();
                }
            });
        }
    }

    private void onInputStream(Response response) {
        InputStream is = null;
        byte[] buf = new byte[4*1024];
        int len;
        FileOutputStream fos = null;
        try{
            File dir = new File(downloadRequest.getDesDir());
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file = new File(dir,downloadRequest.getRename());
            long _currSize = file.length();
            is = response.body().byteStream();
            long length = response.body().contentLength();
            long _totalSize = length;
            boolean append = false;
            ConfigManager.DownloadConfig downloadConfig = downloadRequest.getDownloadConfig();
            if(downloadConfig!=null){
                if(downloadConfig.getTotalSize()>=length){
                    _totalSize = downloadConfig.getTotalSize();
                    append = true;
                }
            }else{
                _currSize = 0;
                downloadConfig = new ConfigManager.DownloadConfig();
                downloadConfig.setUpdateTime(System.currentTimeMillis());
                downloadConfig.setTotalSize(length);
                downloadRequest.setDownloadConfig(downloadConfig);
            }
            fos = new FileOutputStream(file,append);
            long currMs = System.currentTimeMillis();
            long nowMs;
            long sum = 0;
            while ((len = is.read(buf)) != -1){
                sum += len;
                nowMs = System.currentTimeMillis();
                if(nowMs - currMs >= 1000){
                    /** 更新下载速度*/
                    onSpeed(sum,nowMs - currMs);
                    currMs = System.currentTimeMillis();
                    sum = 0;
                }
                fos.write(buf, 0, len);
                fos.flush();
                _currSize += len;
                /** 更新下载进度*/
                onProgressChange(_currSize, _totalSize);
            }
            fos.flush();
            /** 下载完成*/
            onFinish(file);
            mQuit = true;
        }catch (Exception e){
            onSaveConfig();
            e.printStackTrace();
            onError(OnDownloadListener.ERROR_TYPE_EXCEPTION,null);
        }finally{
            try{
                if (is != null)
                    is.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void onSaveConfig() {
        ConfigManager.writeConfig(downloadRequest,downloadRequest.getDownloadConfig());
    }

    private void onSpeed(final long sum, final long dMs) {
        if(onDownloadListener!=null){
            final long byteEverySecond = (long) (sum*1.0/(dMs*1.0/1000));
            XHTTP.handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownloadListener.onSpeed(byteEverySecond,sum,dMs);
                }
            });
        }
    }

    private void onFinish(final File file) {
        onSpeed(0,1);
        ConfigManager.deleteConfig(downloadRequest);
        if(onDownloadListener!=null){
            XHTTP.handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownloadListener.onFinish(file);
                }
            });
        }
    }

    private void onProgressChange(final long currSize, final long totalSize) {
        if(onDownloadListener!=null){
            XHTTP.handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownloadListener.onProgress(currSize,totalSize);
                }
            });
        }
    }

    private void onRedirect(Response response) {
        int redirectCount = downloadRequest.getRedirectCount();
        redirectCount++;
        downloadRequest.setRedirectCount(redirectCount);
        String location = response.header("Location");
        downloadRequest.setUrl(location);
        startDownload();
    }

}
