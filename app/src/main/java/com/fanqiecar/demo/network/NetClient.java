package com.fanqiecar.demo.network;

import android.util.Log;

import com.fanqiecar.demo.network.gsonconverter.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by DuanHl on 2018/3/10.
 */

public class NetClient {

    private static final String TAG = "NetClient";

    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;

    private static final String HEADER_DOMAIN = "domain";

    public static final String HEADER_DOMAIN_PREFIX = HEADER_DOMAIN+": ";

    private final String mBaseUrl = "http://www.zhibo.tv/";

    private NetClient() {
        initOkHttpClient();
        initRetrofit();
    }

    private static class Singleton {
        private static final NetClient INSTANCE = new NetClient();
    }

    public static NetClient getInstance() {
        return Singleton.INSTANCE;
    }

    private void initOkHttpClient() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        return chain.proceed(processRequest(chain.request()));
                    }
                })
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
//                        Log.i(TAG, "response:" + chain.request().url().toString());
                        return chain.proceed(chain.request());
                    }
                })
                .build();
    }

    private void initRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mOkHttpClient)
                .build();
    }

    private Request processRequest(Request request) {
        String domain = request.header(HEADER_DOMAIN);
        if (domain != null && domain.length() != 0) {
            HttpUrl httpUrl = HttpUrl.parse(domain);
            if (httpUrl == null) {
                throw new IllegalArgumentException("'" + request.url().encodedPath() +
                        "' has wrong format '" + HEADER_DOMAIN + "' header.");
            }
            HttpUrl newUrl = request.url().newBuilder()
                    .scheme(httpUrl.scheme())
                    .host(httpUrl.host())
                    .port(httpUrl.port())
                    .build();
            Log.i(TAG, "origin:"+request.url().toString()+"\n       " + newUrl.toString());
            request = request.newBuilder()
                    .removeHeader(HEADER_DOMAIN)
                    .url(newUrl)
                    .build();
        }
        return request;
    }

    public <T> T create(Class<T> clazz) {
        return mRetrofit.create(clazz);
    }

}
