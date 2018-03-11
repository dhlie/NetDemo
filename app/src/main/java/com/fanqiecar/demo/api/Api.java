package com.fanqiecar.demo.api;

import com.fanqiecar.demo.model.AreaOfIP;
import com.fanqiecar.demo.model.City;
import com.fanqiecar.demo.model.Data;
import com.fanqiecar.demo.model.Location;
import com.fanqiecar.demo.model.RespData;
import com.fanqiecar.demo.network.NetClient;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by DuanHl on 2018/3/10.
 */

public interface Api {
    //http://ip.taobao.com/service/getIpInfo.php?ip=63.223.108.42
    @Headers(NetClient.HEADER_DOMAIN_PREFIX + Domain.TAOBAO)
    @GET("/service/getIpInfo.php")
    Observable<RespData<AreaOfIP>> queryIp(@Query("ip") String ip);

    //http://ip.taobao.com/service/getIpInfo.php?ip=63.223.108.42
    @GET("http://ip.taobao.com/service/getIpInfo.php?ip=63.223.108.42")
    Observable<Data> queryIp1();

    //http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=218.4.255.255
    @Headers(NetClient.HEADER_DOMAIN_PREFIX + Domain.SINA)
    @GET("/iplookup/iplookup.php?format=json")
    Observable<City> queryIp2(@Query("ip") String ip);

    //http://gc.ditu.aliyun.com/regeocoding?l=39.938133,116.395739&type=001
    @Headers(NetClient.HEADER_DOMAIN_PREFIX + Domain.ALIYUN)
    @GET("/regeocoding?l=39.938133,116.395739&type=001")
    Observable<Location> queryLocation();
}
