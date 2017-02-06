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


    public static Call download(String url,String desPath, OnDownloadListener onDownloadListener){
        DownloadRequest downloadRequest = new DownloadRequest();
        downloadRequest.setUrl(url);
        downloadRequest.setDesPath(desPath);
        return download(downloadRequest,onDownloadListener);
    }

    public static Call download(final DownloadRequest downloadRequest, final OnDownloadListener onDownloadListener){

        //builder.addHeader("Range", "bytes=" + mVideoDownloadInfo.getDownSize() + '-' + mVideoDownloadInfo.getFileSize());

        File file = new File(downloadRequest.getDesPath());
        if(file.exists()){
            //采用同路径下的配置文件方式记录下载信息
        }

        final Call call = XHTTP.buildCall(downloadRequest);

        if(!XHTTP.isNetAvaliable()){
            if(onDownloadListener!=null){
                onDownloadListener.onError(OnDownloadListener.ERROR_TYPE_NETWORK,null);
            }
            call.cancel();
            return call;
        }

        ThreadManager.getLongPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    if(response.isRedirect() && downloadRequest.getRedirectCount()<DownloadRequest.MAX_REDIRECT_NUM){
                        onRedirect(response, downloadRequest, onDownloadListener);
                        call.cancel();
                    }else if(response.isSuccessful()){
                        onInputStream(response,downloadRequest,onDownloadListener);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return call;
    }

    private static void onInputStream(Response response, DownloadRequest downloadRequest, OnDownloadListener onDownloadListener) {
        InputStream is = null;
        byte[] buf = new byte[4*1024];
        int len;
        FileOutputStream fos = null;
        try{
            File file = new File(downloadRequest.getDesPath());
            is = response.body().byteStream();
            long length = response.body().contentLength();
            fos = new FileOutputStream(file,false);
            while ((len = is.read(buf)) != -1){
                fos.write(buf, 0, len);
                fos.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
            onfailure(e,onDownloadListener);
        }finally{
            try{
                if (is != null) is.close();
            } catch (IOException e){
            }
            try{
                if (fos != null) fos.close();
            } catch (IOException e){
            }

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

    private static void onfailure(final Exception e, final OnDownloadListener onDownloadListener){
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
