package com.yhy.andrutils;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.yhy.utils.core.SysUtils;
import com.yhy.utils.core.ToastUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ToastUtils.longT(APIUtils.getApiByKey("user.regist"));

//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 100);

        ToastUtils.longT(SysUtils.getDeviceId());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ToastUtils.longT(SysUtils.getAppName());
            }
        }, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            ToastUtils.longT(SysUtils.getPhoneNo());
        }
    }
}
