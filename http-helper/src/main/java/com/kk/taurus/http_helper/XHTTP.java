package com.kk.taurus.http_helper;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.kk.taurus.http_helper.bean.BaseResponse;
import com.kk.taurus.http_helper.bean.XRequest;
import com.kk.taurus.http_helper.callback.BeanCallBack;
import com.kk.taurus.http_helper.callback.HttpCallBack;
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
    private static OkHttpClient okHttpClient;
    private static Handler handler = new Handler(Looper.getMainLooper());

    static {
        okHttpClient = new OkHttpClient();
    }

    public static <T extends BaseResponse> Call newGet(final XRequest request, final HttpCallBack<T> httpCallBack){
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
        request(call,httpCallBack);
        return call;
    }

    private static void request(final Call call,final HttpCallBack httpCallBack){
        ThreadManager.getLongPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    if(response.isSuccessful()){
                        onHandleResult(response,httpCallBack);
                    }else{
                        onError(response,httpCallBack);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onFailure(e,httpCallBack);
                }
            }
        });
    }

    private static <T extends BaseResponse> void onHandleResult(final Response response, final HttpCallBack<T> httpCallBack) {
        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    httpCallBack.onSuccess(response);
                }
            });
            String string = new String(response.body().bytes(),"UTF-8");
            Log.i(TAG,string);
            if(httpCallBack instanceof BeanCallBack){
                Gson gson = new Gson();
                final T t = gson.fromJson(string, ((BeanCallBack) httpCallBack).getType());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((BeanCallBack) httpCallBack).onResponseBean(t);
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

    public static <T extends BaseResponse> Call newPost(XRequest request,HttpCallBack<T> httpCallBack){
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
        request(call,httpCallBack);
        return call;
    }

}
