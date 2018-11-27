package com.yhy.andrutils;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.yhy.utils.core.APIUtils;
import com.yhy.utils.core.LogUtils;
import com.yhy.utils.core.StatusBarUtils;
import com.yhy.utils.core.SysUtils;
import com.yhy.utils.core.ToastUtils;
import com.yhy.utils.helper.PermissionHelper;
import com.yhy.utils.helper.SMSCodeAutoFillHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        StatusBarUtils.darkMode(this, true);

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

        SMSCodeAutoFillHelper.getInstance().subscribe(this, 4, new SMSCodeAutoFillHelper.OnResolveListener() {
            @Override
            public void onResolved(String address, String code) {
                ToastUtils.longT("收到来自【" + address + "】的验证码【" + code + "】");
            }

            @Override
            public void onError(int code, String error) {
                switch (code) {
                    case SMSCodeAutoFillHelper.CODE_ERROR_PERMISSION:
                        ToastUtils.longT("读取短信权限已被拒绝，请手动开启");
                        break;
                    case SMSCodeAutoFillHelper.CODE_ERROR_PARSE:
                        ToastUtils.longT("短信验证码解析失败，请手动填写");
                        break;
                    case SMSCodeAutoFillHelper.CODE_ERROR_FOUND:
                        ToastUtils.longT("短信读取失败，可能是系统开启了“验证码安全保护”功能");
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSCodeAutoFillHelper.getInstance().unSubscribe(this);
    }
}
