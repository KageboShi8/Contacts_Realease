package com.example.kageboshi.contacts_debug.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kageboshi.contacts_debug.R;
import com.example.kageboshi.contacts_debug.adapter.ContactAdapter;
import com.example.kageboshi.contacts_debug.http.DownloadAsync;
import com.example.kageboshi.contacts_debug.http.RetrofitFactory;
import com.example.kageboshi.contacts_debug.http.RetrofitService;
import com.example.kageboshi.contacts_debug.http.model.ContactResponseModel;
import com.example.kageboshi.contacts_debug.http.model.NotificationResponseModel;
import com.example.kageboshi.contacts_debug.http.model.VersionResponseModel;
import com.example.kageboshi.contacts_debug.utils.Constants;
import com.example.kageboshi.contacts_debug.utils.ContactsUtils;
import com.example.kageboshi.contacts_debug.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.KeyEvent.KEYCODE_MEDIA_EJECT;
import static android.view.KeyEvent.KEYCODE_MEDIA_RECORD;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private String token;
    private Toolbar toolbarContact;
    private RecyclerView recyclerContacts;
    private Button buttonDownload;
    private Button buttonClear;
    private TextView textTitle;
    private List<ContactResponseModel.DataBean.ContactsBean> contactsList = new ArrayList<ContactResponseModel.DataBean.ContactsBean>();
    private TextView tv_name;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constants.CONTACTS_HANDLER_CODE) {
                if (!contactsList.isEmpty() && null != contactsList) {
                    writeintoPhone();
                    recyclerContacts.setVisibility(View.VISIBLE);
                    showContacts();
                    ToastUtil.show(getApplicationContext(), R.string.contacts_downloaded);
                } else {
                    ToastUtil.show(getApplicationContext(), R.string.contacts_empty);
                }
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Intent intent = getIntent();
        token = intent.getStringExtra(Constants.TOKEN_KEY);
        //  Log.e("TAG",string);
        initView();
        toolbarSetting();
        if (Build.VERSION.SDK_INT >= 23) {
            permissionCheck();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                notificationCheck();
            }
        }).start();

    }

    private void initView() {
        tv_name = ((TextView) findViewById(R.id.tv_login_name));
        toolbarContact = ((Toolbar) findViewById(R.id.toolbar_contact));
        recyclerContacts = ((RecyclerView) findViewById(R.id.recycler_contacts));
        SharedPreferences sp = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        String name = sp.getString(Constants.INFO_NAME, "");
        tv_name.setText(getResources().getString(R.string.user_name) + ": " + name);
        buttonDownload = ((Button) findViewById(R.id.download));
        buttonClear = ((Button) findViewById(R.id.clear));
        buttonClear.setOnClickListener(this);
        buttonDownload.setOnClickListener(this);
    }

    private void toolbarSetting() {
        toolbarContact.setTitle("");
        setSupportActionBar(toolbarContact);
        textTitle = ((TextView) toolbarContact.findViewById(R.id.textTitle));
        textTitle.setText(getResources().getText(R.string.contacts_Activity));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarContact.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_version_number:
                        PackageManager packageManager = getPackageManager();
                        try {
                            String versionName = packageManager.getPackageInfo(getPackageName(), 0).versionName;
                            Log.e("TAG", versionName);
                            ToastUtil.show(getApplicationContext(), getResources().getString(R.string.version_number_is) + versionName);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        break;
                    case R.id.menu_update_address:
                        ToastUtil.show(getApplicationContext(), "暂未开放此功能");
                        break;
                    case R.id.menu_update:
                        //  ToastUtil.show(getApplicationContext(), R.string.no_update);
                        getHttpUpdateInfo();
                        break;
                }
                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permissionCheck() {
        if (checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, Constants.CONTACTS_REQUEST_CODE);
            return;
        }
    }

    private void notificationCheck() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL_DEBUG_TEST)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        retrofit.create(RetrofitService.class).getNotificationMessage()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NotificationResponseModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NotificationResponseModel notificationResponseModel) {
                        Log.d("response", "response ok");
                        int code = notificationResponseModel.getCode();
//                        Log.d("response", "code=" + code);

//                        Log.d("response","message="+message);
                        if (code == 0) {
                            String message = notificationResponseModel.getMessage();
                            if (!isMessageShown(message)) {
                                alertNotification(message);
                            }

                        } else {
                            Log.d("response", "no msg");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("response", "response error");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getHttpUpdateInfo() {
        PackageManager packageManager = getPackageManager();
        try {
            final String versionName = packageManager.getPackageInfo(getPackageName(), 0).versionName;
            //final double version = Double.parseDouble(versionName);
            Log.d("Tag", "versionName=" + versionName);
            if (null != versionName) {
                RetrofitFactory.getInstance(ContactActivity.this)
                        .getVersion(versionName).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<VersionResponseModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(VersionResponseModel versionResponseModel) {
                        Log.d("Tag", "success");
                        int code = versionResponseModel.getCode();
                        Log.d("Tag", code + "");
                        if (code == 0) {
                            String newVersion = versionResponseModel.getData().getVersion();
                            Log.d("Tag", "newVersion=" + newVersion);
                            if (newVersion.equals(versionName) || versionName.equals(0)) {
                                ToastUtil.show(getApplicationContext(), R.string.no_update);
                            } else {
                                String url = versionResponseModel.getData().getUrl();
                                if (null != url)
                                    Log.d("Tag", url);
                                alertDownload(url);
                            }
                        } else {
                            ToastUtil.show(getApplicationContext(), R.string.no_update);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Tag", "error");
                        ToastUtil.show(getApplicationContext(), R.string.no_update);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

            } else {
                ToastUtil.show(getApplicationContext(), R.string.no_update);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean isMessageShown(String message) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        String localMessage = sharedPreferences.getString(Constants.INFO_MESSAGE, "");
        if (localMessage != message) {
            return false;
        }
        return true;
    }

    private void alertNotification(final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveMessageInfo(message);
            }
        }).show();
    }

    private void alertDownload(final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("检测到有新版本app 是否下载?");
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadAPK(url);
                Toast.makeText(getApplicationContext(), "开始下载", Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void saveMessageInfo(String message) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(Constants.INFO_MESSAGE, message);
        edit.commit();
    }

    private void downloadAPK(String url) {
        progressDialog = new ProgressDialog(ContactActivity.this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在下载...");
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        DownloadAsync downloadAsync = new DownloadAsync(this, progressDialog);
        downloadAsync.execute(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // KEYCODE_MEDIA_RECORD = 130;
        if (keyCode == KEYCODE_MEDIA_RECORD) {
            buttonDownload.callOnClick();
            return true;
        }
        //KEYCODE_MEDIA_EJECT=129;
        if (keyCode == KEYCODE_MEDIA_EJECT) {
            buttonClear.callOnClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.CONTACTS_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                ToastUtil.show(this, R.string.permission_denied);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //   后退键的逻辑
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case android.R.id.home:
//                finish();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.download:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadContactInfo();
                    }
                }).start();
                break;
            case R.id.clear:
                ContactsUtils.deleteAll(getApplicationContext());
                ToastUtil.show(getApplicationContext(), R.string.contacts_clear);
                recyclerContacts.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void downloadContactInfo() {
        RetrofitFactory.getInstance(ContactActivity.this).getContacts(token, "0", "0", 5)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ContactResponseModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ContactResponseModel contactResponseModel) {
                        Log.e("TAG", "SUCCESS");
                        contactsList = contactResponseModel.getData().getContacts();
                        if (contactsList != null && !contactsList.isEmpty()) {
                            Message msg = new Message();
                            msg.what = Constants.CONTACTS_HANDLER_CODE;
                            handler.sendMessage(msg);
                        } else {
                            ToastUtil.show(getApplicationContext(), R.string.contacts_empty);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.show(getApplicationContext(), getResources().getString(R.string.download_failure));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showContacts() {
        recyclerContacts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ContactAdapter adapter = new ContactAdapter(getApplicationContext(), contactsList);
        recyclerContacts.setAdapter(adapter);
    }

    private void writeintoPhone() {
        for (int i = 0; i < contactsList.size(); i++) {
            String name = contactsList.get(i).getName();
            String phone = contactsList.get(i).getPhone();
            ContactsUtils.addContact(getApplicationContext(), name, phone);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
