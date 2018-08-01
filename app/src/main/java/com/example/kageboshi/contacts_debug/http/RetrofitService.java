package com.example.kageboshi.contacts_debug.http;

import com.example.kageboshi.contacts_debug.http.model.ContactResponseModel;
import com.example.kageboshi.contacts_debug.http.model.LoginRequestModel;
import com.example.kageboshi.contacts_debug.http.model.LoginResponseModel;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitService {
    @Headers({"Content-Type: application/json",
            "Accept: application/json",
            "User-Agent:dalvik/2.1.0 (linux; u; android 7.0; redmi note 4x miui/v9.5.2.0.ncfcnfa"})
    @POST("login/")
    Observable<LoginResponseModel> postCall(@Body RequestBody requestBody);




    @Headers({"User-Agent:dalvik/2.1.0 (linux; u; android 7.0; redmi note 4x miui/v9.5.2.0.ncfcnfa"})
    @GET("contacts")
    Observable<ContactResponseModel> getContacts(@Header("token") String token, @Query("ver") String ver, @Query("typ") String typ, @Query("nm") int nm);
}

