package com.fanqiecar.demo.network;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by duanhl on 2018/3/12.
 */

public class ProgressRequestBody extends RequestBody {

    private final RequestBody requestBody;
    private final ProgressListener progressListener;
    private BufferedSink bufferedSink;

    public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener) {
        this.requestBody = requestBody;
        this.progressListener = progressListener;
    }

    @Override public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override public long contentLength() throws IOException {
        try {
            return requestBody.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink));
        }
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long bytesWirtten = 0L;
            long contentLength = 0L;
            long time;

            @Override public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0L) {
                    contentLength = contentLength();
                }
                bytesWirtten += byteCount;

                long ct = System.currentTimeMillis();
                if (ct - time >= 16) {
                    time = ct;
                    progressListener.update(bytesWirtten, contentLength, bytesWirtten == contentLength);
                } else {
                    if (bytesWirtten == contentLength) {
                        progressListener.update(bytesWirtten, contentLength, bytesWirtten == contentLength);
                    }
                }
            }
        };
    }

}
