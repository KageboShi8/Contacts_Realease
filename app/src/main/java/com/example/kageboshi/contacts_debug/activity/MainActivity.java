package com.example.kageboshi.contacts_debug.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kageboshi.contacts_debug.R;
import com.example.kageboshi.contacts_debug.activity.ContactActivity;
import com.example.kageboshi.contacts_debug.http.RetrofitFactory;
import com.example.kageboshi.contacts_debug.http.RetrofitService;
import com.example.kageboshi.contacts_debug.http.model.LoginRequestModel;
import com.example.kageboshi.contacts_debug.http.model.LoginResponseModel;
import com.example.kageboshi.contacts_debug.utils.Constants;
import com.example.kageboshi.contacts_debug.utils.ToastUtil;
import com.google.gson.Gson;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarMain;
    private TextView textUser;
    private TextView textPassword;
    private EditText editUser;
    private EditText editPassword;
    private Button buttonLogin;
    private TextView textTitle;
    private String userInfo;
    private String passwordInfo;
    private String imei;
    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        toolbarSetting();
        if (Build.VERSION.SDK_INT >= 23) {
            permissionCheck();
        }
        if (getUserInfo()) {
            getHttpInfo();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permissionCheck() {
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.PHONE_STATE_REQUEST_CODE);
            return;
        }
    }


    private void toolbarSetting() {
        toolbarMain.setTitle("");
        setSupportActionBar(toolbarMain);
        textTitle = ((TextView) toolbarMain.findViewById(R.id.textTitle));
        textTitle.setText(getResources().getText(R.string.login));
        toolbarMain.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_version_number:
                        PackageManager packageManager = getPackageManager();
                        try {
                            String versionName = packageManager.getPackageInfo(getPackageName(), 0).versionName;
                            Log.e("aaa", versionName);
                            ToastUtil.show(getApplicationContext(), getResources().getString(R.string.version_number_is) + versionName);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        break;
                    case R.id.menu_update_address:
                        ToastUtil.show(getApplicationContext(), "暂未开放此功能");
                        break;
                    case R.id.menu_update:
                        ToastUtil.show(getApplicationContext(), R.string.no_update);
                        break;
                }

                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private void bindView() {
        toolbarMain = ((Toolbar) findViewById(R.id.toolbar_main));
        textUser = ((TextView) findViewById(R.id.user));
        textPassword = ((TextView) findViewById(R.id.password));
        editUser = ((EditText) findViewById(R.id.user_edit));
        editPassword = ((EditText) findViewById(R.id.password_edit));
        buttonLogin = ((Button) findViewById(R.id.login));
        buttonLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                getLoginInfo();
                checkLoginInfo();
                if (!userInfo.equals("") && !passwordInfo.equals("") && null != imei) {
                    saveUserInfo();
                    getHttpInfo();
                }
                break;
        }
    }

    private void saveUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(Constants.INFO_NAME, userInfo);
        edit.putString(Constants.INFO_PASSWORD, passwordInfo);
        edit.commit();
    }


    private boolean getUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        passwordInfo = sharedPreferences.getString(Constants.INFO_PASSWORD, "");
        userInfo = sharedPreferences.getString(Constants.INFO_NAME, "");
        if (passwordInfo != "" && userInfo != "") {
            editUser.setText(userInfo);
            editPassword.setText(passwordInfo);
            return true;
        } else {
            return false;
        }
    }


    private void getHttpInfo() {
        RetrofitFactory.getInstance(MainActivity.this)
                .postCall(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), initPostInfo()))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<LoginResponseModel>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LoginResponseModel loginResponseModel) {
                Log.e("Tag", "success");
                int code = loginResponseModel.getCode();
                Log.e("Tag", "code=" + code);
                if (code == 0) {
                    String token = loginResponseModel.getData().getToken();
                    if (null != token) {
                        toContactsActivity(token);
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), R.string.login_fail);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Tag", "error");
                ToastUtil.show(getApplicationContext(), R.string.network_error);
            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void toContactsActivity(String token) {
        Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra(Constants.TOKEN_KEY, token);
        startActivity(intent);
    }

    private String initPostInfo() {
        LoginRequestModel loginRequestModel = new LoginRequestModel();
        LoginRequestModel.DataBean dataBean = new LoginRequestModel.DataBean();

        dataBean.setUsername(userInfo);
        dataBean.setPassword(passwordInfo);
        //     Log.e("imei", imei);

        dataBean.setIem(imei);

        loginRequestModel.setVer(0);
        loginRequestModel.setTyp(0);
        loginRequestModel.setData(dataBean);

        Gson gson = new Gson();
        String json = gson.toJson(loginRequestModel);

        Log.e("gson", json);

        return json;
    }


    private void checkLoginInfo() {
        if (userInfo.equals("")) {
            ToastUtil.show(this, R.string.name_illegal);
            return;
        }
        if (passwordInfo.equals("")) {
            ToastUtil.show(this, R.string.password_illegal);
            return;
        }
        if (null == imei) {
            Log.e("TAG", "imei is empty");
            return;
        }
    }

    private void getLoginInfo() {
        userInfo = editUser.getText().toString();
        passwordInfo = editPassword.getText().toString();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        imei = telephonyManager.getDeviceId();
        Log.e("TAG", "imei=" + imei);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.PHONE_STATE_REQUEST_CODE) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ToastUtil.show(this, R.string.permission_denied);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getUserInfo()) {
            editPassword.setText(passwordInfo);
            editUser.setText(userInfo);
        }
    }
}
