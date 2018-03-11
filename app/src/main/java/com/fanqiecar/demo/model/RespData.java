package com.fanqiecar.demo.model;

/**
 * Created by DuanHl on 2018/3/11.
 */

public class RespData<T> {

    private int code;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
