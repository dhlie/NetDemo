package com.fanqiecar.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fanqiecar.demo.api.Api;
import com.fanqiecar.demo.model.AreaOfIP;
import com.fanqiecar.demo.model.City;
import com.fanqiecar.demo.model.Data;
import com.fanqiecar.demo.model.RespData;
import com.fanqiecar.demo.network.NetClient;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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

    }

    public void btn4(View view) {

    }

    public void btn5(View view) {

    }
}
