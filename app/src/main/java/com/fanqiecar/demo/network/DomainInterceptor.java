package com.fanqiecar.demo.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by duanhl on 2018/3/13.
 */

public class DomainInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String domain = request.header(NetService.HEADER_DOMAIN);
        if (domain != null && domain.length() != 0) {
            HttpUrl httpUrl = HttpUrl.parse(domain);
            if (httpUrl == null) {
                throw new IllegalArgumentException("'" + request.url().encodedPath() +
                        "' has wrong format '" + NetService.HEADER_DOMAIN + "' header.");
            }
            HttpUrl newUrl = request.url().newBuilder()
                    .scheme(httpUrl.scheme())
                    .host(httpUrl.host())
                    .port(httpUrl.port())
                    .build();
            request = request.newBuilder()
                    .removeHeader(NetService.HEADER_DOMAIN)
                    .url(newUrl)
                    .build();
        }

        return chain.proceed(request);
    }

}
