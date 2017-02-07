package com.kk.taurus.http_helper.download;

/**
 * Created by Taurus on 2017/2/6.
 */

public class DownloadTask {

    private boolean cancel;
    private DownloadThread downloadThread;

    public DownloadThread getDownloadThread() {
        return downloadThread;
    }

    public void setDownloadThread(DownloadThread downloadThread) {
        this.downloadThread = downloadThread;
    }

    public void cancel(){
        if(downloadThread!=null){
            downloadThread.quit();
            cancel = true;
        }
    }

    public boolean isCancel() {
        return cancel;
    }
}
