package com.example.kageboshi.contacts_debug.http;

import android.content.Context;
import android.util.Log;

import com.example.kageboshi.contacts_debug.utils.Constants;
import com.example.kageboshi.contacts_debug.utils.WifiUtil;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.kageboshi.contacts_debug.utils.Constants.BASE_URL_DEBUG;
import static com.example.kageboshi.contacts_debug.utils.Constants.BASE_URL_RELEASE;

public class RetrofitFactory {
    private static RetrofitService retrofitService;
    private static Retrofit retrofit;

    public static RetrofitService getInstance(Context context) {
        if (null == retrofitService) {
            synchronized (RetrofitFactory.class) {
                if (null == retrofitService) {
                    retrofitService = getInstanceRetrofit(context).create(RetrofitService.class);
                }
            }
        }
        return retrofitService;
    }


    private static Retrofit getInstanceRetrofit(Context context) {
        String url = "";
        if (WifiUtil.isWifi(context)) {
            url = BASE_URL_DEBUG;
            Log.e("net","wifi");
        } else {
            url = BASE_URL_RELEASE;
            Log.e("net","mobile");
        }

        if (null == retrofit) {
            synchronized (RetrofitFactory.class) {
                if (null == retrofit) {
                    retrofit = new Retrofit.Builder().baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }
}
