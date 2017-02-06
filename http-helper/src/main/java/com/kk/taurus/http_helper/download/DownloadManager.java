package com.kk.taurus.http_helper.download;

import com.kk.taurus.http_helper.XHTTP;
import com.kk.taurus.http_helper.callback.OnDownloadListener;
import com.kk.taurus.http_helper.thread.ThreadManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Taurus on 2017/2/6.
 */

public class DownloadManager {

    private static DownloadThread downloadThread;

    public static void stopDownload(){
        if(downloadThread!=null){
            downloadThread.quit();
        }
    }


    public static Call download(String url,String desDir, String fileName, OnDownloadListener onDownloadListener){
        DownloadRequest downloadRequest = new DownloadRequest();
        downloadRequest.setUrl(url);
        downloadRequest.setDesDir(desDir);
        downloadRequest.setRename(fileName);
        return download(downloadRequest,onDownloadListener);
    }

    public static Call download(final DownloadRequest downloadRequest, final OnDownloadListener onDownloadListener){

        //采用同路径下的配置文件方式记录下载信息
        ConfigManager.DownloadConfig downloadConfig = ConfigManager.loadConfig(downloadRequest);
        if(downloadConfig!=null){
            downloadRequest.setDownloadConfig(downloadConfig);
            downloadRequest.addHeader("Range","bytes=" + downloadConfig.getCurrSize() + "-" + downloadConfig.getTotalSize());
        }

        final Call call = XHTTP.buildCall(downloadRequest);

        if(!XHTTP.isNetAvaliable()){
            if(onDownloadListener!=null){
                onDownloadListener.onError(OnDownloadListener.ERROR_TYPE_NETWORK,null);
            }
            call.cancel();
            return call;
        }

        ThreadManager.getLongPool().execute(downloadThread = new DownloadThread(call,downloadRequest,onDownloadListener));

//        ThreadManager.getLongPool().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    onStart(onDownloadListener);
//                    Response response = call.execute();
//                    if(response.isRedirect() && downloadRequest.getRedirectCount()<DownloadRequest.MAX_REDIRECT_NUM){
//                        onRedirect(response, downloadRequest, onDownloadListener);
//                        call.cancel();
//                    }else if(response.isSuccessful()){
//                        onInputStream(response,downloadRequest,onDownloadListener);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        return call;
    }

    private static void onStart(final OnDownloadListener onDownloadListener) {
        if(onDownloadListener!=null){
            XHTTP.handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownloadListener.onStart();
                }
            });
        }
    }

    private static void onInputStream(Response response, DownloadRequest downloadRequest, OnDownloadListener onDownloadListener) {
        InputStream is = null;
        byte[] buf = new byte[4*1024];
        int len;
        FileOutputStream fos = null;
        try{
            File file = new File(downloadRequest.getDesDir(),downloadRequest.getRename());
            is = response.body().byteStream();
            long length = response.body().contentLength();
            long _totalSize = length;
            ConfigManager.DownloadConfig downloadConfig = downloadRequest.getDownloadConfig();
            if(downloadConfig!=null && downloadConfig.getTotalSize()>=length){
                _totalSize = downloadConfig.getTotalSize();
            }
            fos = new FileOutputStream(file,false);
            long _currSize = file.length();
            long currMs = System.currentTimeMillis();
            long nowMs = 0;
            long sum = 0;
            while ((len = is.read(buf)) != -1){
                sum += len;
                nowMs = System.currentTimeMillis();
                if(nowMs - currMs >= 1000){
                    /** 更新下载速度*/
                    onSpeed(sum,nowMs - currMs,onDownloadListener);
                    currMs = System.currentTimeMillis();
                    sum = 0;
                }
                fos.write(buf, 0, len);
                _currSize += len;
                /** 更新下载进度*/
                onProgressChange(_currSize, _totalSize,onDownloadListener);
            }
            fos.flush();
            /** 下载完成*/
            onFinish(file,onDownloadListener);
        }catch (Exception e){
            e.printStackTrace();
            onFailure(e,onDownloadListener);
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

    private static void onSpeed(long sum, long dMs, final OnDownloadListener onDownloadListener) {
        if(onDownloadListener!=null){
            final float speed = (float) (((sum/1024)*1.0)/(dMs*1.0/1000));
            XHTTP.handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownloadListener.onSpeed(speed);
                }
            });
        }
    }

    private static void onFinish(final File file, final OnDownloadListener onDownloadListener) {
        if(onDownloadListener!=null){
            XHTTP.handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownloadListener.onFinish(file);
                }
            });
        }
    }

    private static void onProgressChange(final long currSize, final long totalSize, final OnDownloadListener onDownloadListener) {
        if(onDownloadListener!=null){
            XHTTP.handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownloadListener.onProgress(currSize,totalSize);
                }
            });
        }
    }

    private static void onRedirect(Response response, DownloadRequest downloadRequest, OnDownloadListener onDownloadListener) {
        int redirectCount = downloadRequest.getRedirectCount();
        redirectCount++;
        downloadRequest.setRedirectCount(redirectCount);
        String location = response.header("Location");
        downloadRequest.setUrl(location);
        download(downloadRequest,onDownloadListener);
    }

    private static void onFailure(final Exception e, final OnDownloadListener onDownloadListener){
        if(onDownloadListener!=null){
            XHTTP.handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownloadListener.onFailure(e);
                }
            });
        }
    }

}
