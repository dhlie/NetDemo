package com.fanqiecar.demo;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fanqiecar.demo.api.Api;
import com.fanqiecar.demo.model.AreaOfIP;
import com.fanqiecar.demo.model.City;
import com.fanqiecar.demo.model.Data;
import com.fanqiecar.demo.model.RespData;
import com.fanqiecar.demo.model.ShareWords;
import com.fanqiecar.demo.model.UploadResult;
import com.fanqiecar.demo.network.NetClient;
import com.fanqiecar.demo.network.ProgressListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void btn1(View view) {
        NetClient.getInstance().create(Api.class)
//                .queryIp("63.223.108.42")
                .queryIp1()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Data>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Data areaOfIP) {
                        showToast(areaOfIP.getData().getCity());
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast("error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void btn2(View view) {
        NetClient.getInstance().create(Api.class)
                .queryIp2("63.223.108.42")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<City>() {
                    @Override
                    public void accept(City city) throws Exception {
                        showToast(city.getCountry());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        showToast("error");
                        throwable.printStackTrace();
                    }
                });
    }

    public void btn3(View view) {
        NetClient.getInstance().create(Api.class)
                .getShareWords()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RespData<ShareWords>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RespData<ShareWords> shareWordsRespData) {
                        showToast(shareWordsRespData.getData().getWords());
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast("error");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void btn4(View view) {
        NetClient.getInstance().createDownloadService(Api.class, new ProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                Log.i("NetClient", "read count:"+bytesRead+"  total:"+contentLength+"   finish:"+done);
            }
        })
                .downloadApk("http://dl.lianwifi.com/download/android/WifiKey-3190-goapk-modify-guanwang.apk")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("NetClient", "onSubscribe-------");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("NetClient", "onComplete-------");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("NetClient", "onError-------");
                    }
                });
    }

    public void btn5(View view) {
        RequestBody parambody = RequestBody.create(MediaType.parse("multipart/form-data"), "78632a2036aebfea36309e4a0d34f743*2166");

        File file = new File(Environment.getExternalStorageDirectory(), "1.jpg");
        RequestBody fbody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("head_img", file.getName(), fbody);

//        Map<String, RequestBody> params = new HashMap<>();
//        params.put("token", parambody);


        RespData<UploadResult> d = null;
        NetClient.getInstance().createUploadService(Api.class, new ProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                Log.i("NetClient", "read count:"+bytesRead+"  total:"+contentLength+"   finish:"+done);
            }
        })
                .upload("http://rest.zhibo.tv/user/update-head-img/", part, parambody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RespData<UploadResult>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("NetClient", "onSubscribe-------");
                    }

                    @Override
                    public void onNext(RespData<UploadResult> o) {
                        Log.i("NetClient", "onNext-------"+o.getData().getConetnt());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.i("NetClient", "onError-------");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("NetClient", "onComplete-------");
                    }
                });
    }

    public void btn6(View view) {
        Api api = NetClient.getInstance().create(Api.class);
    }
}
