package com.yhy.andrutils;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.yhy.utils.core.APIUtils;
import com.yhy.utils.core.LogUtils;
import com.yhy.utils.core.SysUtils;
import com.yhy.utils.core.ToastUtils;
import com.yhy.utils.helper.PermissionHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiUpload = APIUtils.getByKey("sys.common.upload.avatar");
        LogUtils.i(apiUpload);
        APIUtils.set("global.user.id", 1024);
        apiUpload = APIUtils.getByKey("sys.common.upload.avatar");
        LogUtils.i(apiUpload);

        findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.getInstance()
                        .permissions(Manifest.permission.CAMERA)
                        .request(new PermissionHelper.SimplePermissionCallback() {
                            @Override
                            public void onGranted() {
                                ToastUtils.shortT("已经同意拍照");
                            }

                            @Override
                            public void onDenied() {
                                ToastUtils.shortT("拒绝了拍照");
                            }
                        });
            }
        });

        findViewById(R.id.tv_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.getInstance()
                        .permissions(Manifest.permission.CALL_PHONE)
                        .request(new PermissionHelper.SimplePermissionCallback() {
                            @Override
                            public void onGranted() {
                                ToastUtils.shortT("已经同意打电话");
                            }

                            @Override
                            public void onDenied() {
                                ToastUtils.shortT("拒绝了打电话");
                            }
                        });
            }
        });
        findViewById(R.id.tv_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.callPhone("18313889251");
            }
        });

        findViewById(R.id.tv_sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.getInstance()
                        .permissions(Manifest.permission.SEND_SMS)
                        .request(new PermissionHelper.SimplePermissionCallback() {
                            @Override
                            public void onGranted() {
                                ToastUtils.shortT("已经同意发短信");
                            }

                            @Override
                            public void onDenied() {
                                ToastUtils.shortT("拒绝了发短信");
                            }
                        });
            }
        });

        findViewById(R.id.tv_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.getInstance()
                        .permissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE)
                        .request(new PermissionHelper.PermissionCallback() {
                            @Override
                            public void onGranted(List<String> granted) {
                                for (String permission : granted) {
                                    Log.i("PermissionResult", "已同意：" + permission);
                                }
                            }

                            @Override
                            public void onDenied(List<String> denied, List<String> forever) {
                                for (String permission : denied) {
                                    Log.i("PermissionResult", "已拒绝：" + permission);
                                }
                                for (String permission : forever) {
                                    Log.i("PermissionResult", "已永久拒绝：" + permission);
                                }
                            }
                        });
            }
        });
    }
}
