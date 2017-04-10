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

package com.kk.taurus.http_helper;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.kk.taurus.http_helper.bean.AbsResponse;
import com.kk.taurus.http_helper.bean.XRequest;
import com.kk.taurus.http_helper.callback.HttpCallBack;
import com.kk.taurus.http_helper.callback.ReqCallBack;
import com.kk.taurus.http_helper.thread.ThreadManager;
import com.kk.taurus.http_helper.utils.Utils;

import java.io.File;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Taurus on 2017/2/4.
 */

public class XHTTP {

    private static final String TAG = "XHTTP";
    public static final String CHAR_SET = "UTF-8";
    public static Context context;

    private static final MediaType MEDIA_TYPE_FILE = MediaType.parse("application/octet-stream");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    private static OkHttpClient okHttpClient;
    public static Handler handler = new Handler(Looper.getMainLooper());

    static {
        okHttpClient = new OkHttpClient();
    }

    public static void init(Application application, OkHttpClient.Builder builder){
        context = application.getApplicationContext();
        if(builder!=null){
            okHttpClient = builder.build();
        }
    }

    public static boolean isNetAvailable(){
        if(context==null)
            throw new RuntimeException("context need init, you should call method XHTTP.init() .");
        return Utils.isAvailable(context);
    }

    public static Request.Builder buildRequest(XRequest request){
        Request.Builder builder = new Request.Builder().url(request.getUrl());
        final Map<String,Object> headers = request.getHeaders();
        if(headers.size()>0){
            for(String key : headers.keySet()){
                builder.addHeader(key,headers.get(key).toString());
            }
        }
        return builder;
    }

    public static Call buildCall(Request request){
        return okHttpClient.newCall(request);
    }

    public static Call buildCall(XRequest request){
        return okHttpClient.newCall(buildRequest(request).build());
    }

    public static Call newGet(final XRequest request, final ReqCallBack reqCallBack){
        Request.Builder builder = buildRequest(request);
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
        Call call = buildCall(builder.build());
        request(call,reqCallBack);
        return call;
    }

    private static void request(final Call call,final ReqCallBack reqCallBack){

        if(!isNetAvailable()){
            onError(HttpCallBack.ERROR_TYPE_NETWORK,null,reqCallBack);
            return;
        }

        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    onStart(reqCallBack);
                    Response response = call.execute();
                    if(response.isSuccessful()){
                        onHandleResult(response,reqCallBack);
                    }else{
                        onError(HttpCallBack.ERROR_TYPE_RESPONSE,response,reqCallBack);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onError(HttpCallBack.ERROR_TYPE_EXCEPTION,null,reqCallBack);
                }
            }
        });
    }

    private static void onStart(final HttpCallBack httpCallBack){
        if(httpCallBack!=null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    httpCallBack.onStart();
                }
            });
        }
    }

    private static <T extends AbsResponse> void onHandleResult(final Response response, final ReqCallBack<T> reqCallBack) throws Exception {
        handler.post(new Runnable() {
            @Override
            public void run() {
                reqCallBack.onSuccess(response);
            }
        });
        if(response.body()!=null){
            String string = new String(response.body().bytes(),CHAR_SET);
            Log.i(TAG,string);
            final T result = reqCallBack.getResponseInstance();
            result.data = JSON.parseObject(string,reqCallBack.getResponseInstance().getType());
            if(result!=null){
                result.code = response.code();
                result.message = response.message();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    reqCallBack.onResponseBean(result);
                }
            });
        }
    }

    private static void onError(final int errorType, final Response response, final HttpCallBack httpCallBack){
        if(httpCallBack!=null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    httpCallBack.onError(errorType,response);
                }
            });
        }
    }

    public static Call newPost(XRequest request,ReqCallBack reqCallBack){
        Request.Builder builder = buildRequest(request);
        Map<String,Object> params = request.getParams();
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for(String key : params.keySet()){
            Object value = params.get(key);
            if(value instanceof File){
                File param = (File) value;
                bodyBuilder.addFormDataPart(key, param.getName(), RequestBody.create(MEDIA_TYPE_FILE, param));
            }else{
                String param = value.toString();
                bodyBuilder.addFormDataPart(key, param);
            }
        }
        builder.post(bodyBuilder.build());
        Call call = buildCall(builder.build());
        request(call,reqCallBack);
        return call;
    }

    public static Call newPostJSON(XRequest request,Object jsonBean, ReqCallBack reqCallBack){
        Request.Builder builder = buildRequest(request);
        //post form params
        Map<String, Object> params = request.getParams();
        builder.post(buildFormBuilder(params).build());
        //post json
        String json = JSON.toJSONString(jsonBean);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON,json);
        builder.put(requestBody);
        Call call = buildCall(builder.build());
        request(call,reqCallBack);
        return call;
    }

    private static FormBody.Builder buildFormBuilder(Map<String, Object> params){
        FormBody.Builder builder = new FormBody.Builder();
        if(params!=null && params.size()>0){
            for(String key:params.keySet()){
                builder.add(key,params.get(key).toString());
            }
        }
        return builder;
    }

}
