package com.kk.taurus.http_helper.bean;

import java.io.Serializable;

/**
 * Created by Taurus on 2017/2/4.
 */

public abstract class BaseResponse<T> implements Serializable {
    public int code;
    public String message;
    public T result;
}
