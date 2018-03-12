package com.fanqiecar.demo.network;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by DuanHl on 2018/3/10.
 */

public class NetClient {

    private static final String TAG = "NetClient";

    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;

    private static final String HEADER_DOMAIN = "domain";

    public static final String HEADER_DOMAIN_PREFIX = HEADER_DOMAIN+": ";

    private HttpLoggingInterceptor.Level LOG_LEVEL = HttpLoggingInterceptor.Level.BODY;

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
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        return chain.proceed(processRequest(chain.request()));
                    }
                })
                .addInterceptor(new HttpLoggingInterceptor().setLevel(LOG_LEVEL))
//                .addNetworkInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        return chain.proceed(chain.request());
//                    }
//                })
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

    public <T> T createDownloadService(Class<T> clazz, final ProgressListener progressListener) {
        OkHttpClient client = mOkHttpClient.newBuilder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                                .build();
                    }
                })
                .build();
        Retrofit retrofit = mRetrofit.newBuilder().client(client).build();
        return retrofit.create(clazz);
    }

    public <T> T createUploadService(Class<T> clazz, final ProgressListener progressListener) {
        OkHttpClient client = mOkHttpClient.newBuilder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        if (request.body() == null) {
                            return chain.proceed(request);
                        }

                        Request progressRequest = request.newBuilder()
                                .method(request.method(), new ProgressRequestBody(request.body(), progressListener))
                                .build();

                        return chain.proceed(progressRequest);
                    }
                })
                .build();
        Retrofit retrofit = mRetrofit.newBuilder().client(client).build();
        return retrofit.create(clazz);
    }
}
