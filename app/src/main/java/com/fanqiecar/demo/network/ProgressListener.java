package com.fanqiecar.demo.network;

/**
 * Created by duanhl on 2018/3/12.
 */

public interface ProgressListener {

    void update(long bytesRead, long contentLength, boolean done);

}
