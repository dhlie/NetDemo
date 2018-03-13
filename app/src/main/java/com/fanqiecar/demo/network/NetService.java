package com.fanqiecar.demo.network;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by duanhl on 2018/3/13.
 */

public class NetService {

    static final String             HEADER_DOMAIN = "domain";

    public static final String      HEADER_DOMAIN_PREFIX = HEADER_DOMAIN+": ";

    private static OkHttpClient     sOkHttpClient;
    private static Retrofit         sRetrofit;

    private static final boolean    SHOW_LOG = true;

    private static final String     BASE_URL = "http://www.zhibo.tv/";

    static {
        initOkHttpClient();
        initRetrofit();
    }

    private static void initOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new DomainInterceptor())
                .cache(getCache());

        if (SHOW_LOG) builder.addInterceptor(new HttpLoggingInterceptor(HttpLoggingInterceptor.Level.BODY));

        sOkHttpClient = builder.build();
    }

    private static void initRetrofit() {
        sRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(sOkHttpClient)
                .build();
    }

    private static Cache getCache() {
        File httpCacheDirectory = new File("", "responses");
        return new Cache(httpCacheDirectory, 10 * 1024 * 1024);// 缓存空间10M
    }

    public static <S> S createService(Class<S> clazz) {
        return sRetrofit.create(clazz);
    }

    public static <S> S createDownloadService(Class<S> clazz, final ProgressListener progressListener) {
        OkHttpClient client = sOkHttpClient.newBuilder()
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
        Retrofit retrofit = sRetrofit.newBuilder().client(client).build();
        return retrofit.create(clazz);
    }

    public static <S> S createUploadService(Class<S> clazz, final ProgressListener progressListener) {
        OkHttpClient client = sOkHttpClient.newBuilder()
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
        Retrofit retrofit = sRetrofit.newBuilder().client(client).build();
        return retrofit.create(clazz);
    }
}
