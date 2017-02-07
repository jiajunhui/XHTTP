package com.kk.taurus.http_helper.download;

import com.kk.taurus.http_helper.XHTTP;
import com.kk.taurus.http_helper.callback.OnDownloadListener;
import com.kk.taurus.http_helper.thread.ThreadManager;

import okhttp3.Call;

/**
 * Created by Taurus on 2017/2/6.
 */

public class DownloadManager {

    public static DownloadTask download(String url,String desDir, String fileName, OnDownloadListener onDownloadListener){
        DownloadRequest downloadRequest = new DownloadRequest();
        downloadRequest.setUrl(url);
        downloadRequest.setDesDir(desDir);
        downloadRequest.setRename(fileName);
        return download(downloadRequest,onDownloadListener);
    }

    public static DownloadTask download(final DownloadRequest downloadRequest, final OnDownloadListener onDownloadListener){

        //采用同路径下的配置文件方式记录下载信息
        ConfigManager.DownloadConfig downloadConfig = ConfigManager.loadConfig(downloadRequest);
        if(downloadConfig!=null){
            downloadRequest.setDownloadConfig(downloadConfig);
            downloadRequest.addHeader("Range","bytes=" + downloadConfig.getCurrSize() + "-" + downloadConfig.getTotalSize());
        }

        DownloadTask downloadTask = new DownloadTask();

        final Call call = XHTTP.buildCall(downloadRequest);

        if(!XHTTP.isNetAvailable()){
            if(onDownloadListener!=null){
                onDownloadListener.onError(OnDownloadListener.ERROR_TYPE_NETWORK,null);
            }
            call.cancel();
            return downloadTask;
        }

        DownloadThread downloadThread = new DownloadThread(call,downloadRequest,onDownloadListener);
        downloadTask.setDownloadThread(downloadThread);
        ThreadManager.getLongPool().execute(downloadThread);

        return downloadTask;
    }

}
