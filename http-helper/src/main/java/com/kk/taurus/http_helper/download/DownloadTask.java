package com.kk.taurus.http_helper.download;

import okhttp3.Call;

/**
 * Created by Taurus on 2017/2/6.
 */

public class DownloadTask {
    private Call call;
    private DownloadThread downloadThread;

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public DownloadThread getDownloadThread() {
        return downloadThread;
    }

    public void setDownloadThread(DownloadThread downloadThread) {
        this.downloadThread = downloadThread;
    }

    public void cancel(){
        if(call!=null){
            call.cancel();
        }
        if(downloadThread!=null){

        }
    }
}
