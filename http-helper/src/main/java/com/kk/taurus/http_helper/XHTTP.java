package com.kk.taurus.http_helper;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.kk.taurus.http_helper.bean.XRequest;
import com.kk.taurus.http_helper.callback.HttpCallBack;
import com.kk.taurus.http_helper.callback.ReqCallBack;
import com.kk.taurus.http_helper.thread.ThreadManager;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Taurus on 2017/2/4.
 */

public class XHTTP {

    private static final String TAG = "XHTTP";
    public static final String CHAR_SET = "UTF-8";
    private static OkHttpClient okHttpClient;
    private static Handler handler = new Handler(Looper.getMainLooper());

    static {
        okHttpClient = new OkHttpClient();
    }

    public static Call newGet(final XRequest request, final ReqCallBack reqCallBack){
        Request.Builder builder = new Request.Builder().url(request.getUrl());
        final Map<String,Object> headers = request.getHeaders();
        if(headers.size()>0){
            for(String key : headers.keySet()){
                builder.addHeader(key,headers.get(key).toString());
            }
        }
        Map<String,Object> params = request.getParams();
        if(params.size()>0){
            StringBuilder sb = new StringBuilder(request.getUrl());
            sb.append("?");
            for(String key : params.keySet()){
                sb.append(key).append("=").append(params.get(key).toString()).append("&");
            }
            String url = sb.toString();
            url = url.substring(0,url.length()-1);
            Log.i(TAG,url);
            builder.url(url);
        }
        Call call = okHttpClient.newCall(builder.build());
        request(call,reqCallBack);
        return call;
    }

    private static void request(final Call call,final ReqCallBack reqCallBack){
        ThreadManager.getLongPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    if(response.isSuccessful()){
                        onHandleResult(response,reqCallBack);
                    }else{
                        onError(response,reqCallBack);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onFailure(e,reqCallBack);
                }
            }
        });
    }

    private static <T> void onHandleResult(final Response response, final ReqCallBack<T> reqCallBack) {
        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    reqCallBack.onSuccess(response);
                }
            });
            if(response.body()!=null){
                String string = new String(response.body().bytes(),CHAR_SET);
                Log.i(TAG,string);
                Gson gson = new Gson();
                final T t = gson.fromJson(string, reqCallBack.getType());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        reqCallBack.onResponseBean(t);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void onFailure(final Exception e, final HttpCallBack httpCallBack){
        if(httpCallBack!=null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    httpCallBack.onFailure(e);
                }
            });
        }
    }

    private static void onError(final Response response, final HttpCallBack httpCallBack){
        if(httpCallBack!=null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    httpCallBack.onError(response);
                }
            });
        }
    }

    public static Call newPost(XRequest request,ReqCallBack reqCallBack){
        Request.Builder builder = new Request.Builder().url(request.getUrl());
        Map<String,Object> headers = request.getHeaders();
        if(headers.size()>0){
            for(String key : headers.keySet()){
                builder.addHeader(key,headers.get(key).toString());
            }
        }
        Map<String,Object> params = request.getParams();
        if(params.size()>0){
            FormBody.Builder formBuilder = new FormBody.Builder();
            for(String key : params.keySet()){
                formBuilder.add(key,params.get(key).toString());
            }
            builder.post(formBuilder.build());
        }
        Call call = okHttpClient.newCall(builder.build());
        request(call,reqCallBack);
        return call;
    }

}
