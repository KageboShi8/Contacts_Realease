package com.example.kageboshi.contacts_debug.utils;

public class Constants {
    public static final int PHONE_STATE_REQUEST_CODE = 100;  //imei请求码

    public static final int CONTACTS_REQUEST_CODE = 200;  //contacts请求码

    public static final int CONTACTS_HANDLER_CODE = 1;

    public static final String SHARED_PREF = "USER_INFO";  //sharedpreference的xml名字

    public static final String INFO_NAME = "name";     //sharedpreference的姓名的key

    public static final String INFO_PASSWORD = "password";  //sharedpreference的密码的key

    public static final String INFO_MESSAGE = "broadcast";

    public static final String BASE_URL_DEBUG_TEST = "http://192.168.188.166:9998/jeesite/app/";

    public static final String BASE_URL_DEBUG = "http://192.168.188.47:9999/api/v1/";

    public static final String BASE_URL_RELEASE = "http://221.226.138.66:9999/api/v1/";

    //副库

//    public static final String BASE_URL_DEBUG = "http://192.168.188.11:7779/api/v1/";
//
//    public static final String BASE_URL_RELEASE = "http://221.226.138.66:7779/api/v1/";

//    public static final String DOWNLOAD_APPANDIX = "dloadv";

    public static final String TOKEN_KEY = "token";  //传token的key


}
