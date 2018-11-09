package com.yhy.utils.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-11-08 11:51
 * version: 1.0.0
 * desc   : 短信验证码自动填充助手
 */
public class SMSCodeAutoFillHelper {
    // 获取成功
    public static final int CODE_SUCCESS = 1024;
    // 权限拒绝
    public static final int CODE_ERROR_PERMISSION = 2048;
    // 正则匹配失败
    public static final int CODE_ERROR_PARSE = 3072;
    // 未查询到短信
    public static final int CODE_ERROR_FOUND = 4096;

    // 缓存
    private static final Map<Object, SMSObserver> CACHES = new HashMap<>();
    // 单例实例
    private volatile static SMSCodeAutoFillHelper instance;

    private Application mApp;

    private SMSCodeAutoFillHelper() {
        if (null != instance) {
            throw new UnsupportedOperationException("Can not instantiate singleton class.");
        }
    }

    /**
     * 获取单例实例
     *
     * @return 单例实例
     */
    public static SMSCodeAutoFillHelper getInstance() {
        if (null == instance) {
            synchronized (SMSCodeAutoFillHelper.class) {
                if (null == instance) {
                    instance = new SMSCodeAutoFillHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化，放在Application中
     *
     * @param app 当前Application
     */
    public void init(Application app) {
        mApp = app;
        CACHES.clear();
    }

    /**
     * 订阅验证码观察者
     *
     * @param tag      标记（注销时需要匹配同一个tag对象）
     * @param length   验证码长度
     * @param listener 监听器
     */
    @SuppressLint("HandlerLeak")
    public void subscribe(Object tag, int length, final OnReadListener listener) {
        Uri uri = Uri.parse("content://sms");

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CODE_SUCCESS:
                        // 获取成功
                        Bundle bundle = msg.getData();
                        if (null != bundle && null != listener) {
                            listener.onResolved(bundle.getString("address"), bundle.getString("code"));
                        }
                        break;
                    case CODE_ERROR_PERMISSION:
                        // 权限拒绝
                        listener.onError(CODE_ERROR_PERMISSION, "Current application can not read sms, because the permission 'READ_SMS' had been denied.");
                        break;
                    case CODE_ERROR_PARSE:
                        // 未解析成功
                        listener.onError(CODE_ERROR_PARSE, "Could not parse sms code as expect, Perhaps you can parse by yourself.");
                        break;
                    case CODE_ERROR_FOUND:
                        // 未查询到短信
                        listener.onError(CODE_ERROR_FOUND, "Could not find record of this message, maybe the switch 'Verification Code Security' has been turned on.");
                        break;
                }
            }
        };

        // 常见短信内容观察者
        SMSObserver observer = new SMSObserver(handler, length);
        mApp.getContentResolver().registerContentObserver(uri, true, observer);
        // 将其缓存
        CACHES.put(tag, observer);
    }

    /**
     * 注销验证码观察者
     *
     * @param tag 标记
     */
    public void unSubscribe(Object tag) {
        SMSObserver observer = CACHES.get(tag);
        if (null != observer) {
            mApp.getContentResolver().unregisterContentObserver(observer);
            CACHES.remove(tag);
        }
    }

    /**
     * 短信内容观察者
     */
    private class SMSObserver extends ContentObserver {
        private Handler mHandler;
        private int mLength;
        private int mId;

        /**
         * 构造函数
         *
         * @param handler 消息处理器
         * @param length  验证码长度
         */
        SMSObserver(Handler handler, int length) {
            super(handler);
            mHandler = handler;
            mLength = length;
            mId = -1;
        }

        /**
         * 当uri内容改变
         *
         * @param selfChange 是否是内容改变
         */
        @Override
        public void onChange(boolean selfChange) {
            PermissionHelper.getInstance().permissions(Manifest.permission.READ_SMS).request(new PermissionHelper.SimplePermissionCallback() {
                @Override
                public void onGranted() {
                    parseSMSCode();
                }

                @Override
                public void onDenied() {
                    mHandler.obtainMessage(CODE_ERROR_PERMISSION).sendToTarget();
                }
            });
        }

        /**
         * 回调函数, 当监听的Uri发生改变时，会回调该方法
         * 需要注意的是当收到短信的时候会回调两次
         * 收到短信一般来说都是执行了两次onchange方法.第一次一般都是raw的这个.
         * 虽然收到了短信.但是短信并没有写入到收件箱里
         *
         * @param selfChange 是否是自己改变
         * @param uri        当前改变的uri
         */
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if ("content://sms/raw".equals(uri.toString())) {
                return;
            }
            onChange(selfChange);
        }

        private void parseSMSCode() {
            Uri uri = Uri.parse("content://sms/inbox");
            // 获取3s以内的未读短信（收件箱）
            Cursor cursor = mApp.getContentResolver().query(uri, new String[]{"_id", "address", "body", "read"}, "read=? and date>?", new String[]{"0", System.currentTimeMillis() - 3000 + ""}, "_id desc");
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(cursor.getColumnIndex("_id"));
                    String address = cursor.getString(cursor.getColumnIndex("address"));
                    String body = cursor.getString(cursor.getColumnIndex("body"));
                    int read = cursor.getInt(cursor.getColumnIndex("read"));

                    if (id == mId) {
                        return;
                    }
                    mId = id;

                    // 断言前后均不是数字的连续 mLength 位数字字符串
                    Pattern pattern = Pattern.compile("(?<!\\d)(\\d{" + mLength + "})(?!\\d)");
                    Matcher matcher = pattern.matcher(body);
                    if (matcher.find()) {
                        String code = matcher.group(1);

                        // 创建消息
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("address", address);
                        bundle.putString("code", code);

                        // 发送消息
                        message.what = CODE_SUCCESS;
                        message.setData(bundle);
                        mHandler.sendMessage(message);

                        // 更新短信状态为已读
                        readSMS();
                    } else {
                        mHandler.obtainMessage(CODE_ERROR_PARSE).sendToTarget();
                    }
                } else {
                    // 如果cursor为空，说明未查询到相关记录
                    // 部分手机有“验证码安全保护”功能，此时获取不到验证码的相关记录
                    mHandler.obtainMessage(CODE_ERROR_FOUND).sendToTarget();
                }
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        }

        /**
         * 更新短信状态为已读
         */
        private void readSMS() {
            ContentValues values = new ContentValues();
            values.put("read", 1);
            // 更新
            mApp.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=?", new String[]{mId + ""});
        }
    }

    /**
     * 短信验证码监听器
     */
    public interface OnReadListener {

        /**
         * 成功处理验证码时将被回调
         *
         * @param address 发送验证码的号码
         * @param code    验证码
         */
        void onResolved(String address, String code);

        /**
         * 发送错误时将被回调
         *
         * @param code  错误码
         * @param error 错误消息
         */
        void onError(int code, String error);
    }
}
