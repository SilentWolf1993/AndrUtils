package com.yhy.utils.helper;

import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-02-02 14:57
 * version: 1.0.0
 * desc   : 获取验证码倒计时助手
 */
public class CaptchaDownHelper {
    // 构造器
    private Builder mBuilder;

    /**
     * 构造方法
     *
     * @param builder 构造器
     */
    private CaptchaDownHelper(Builder builder) {
        this.mBuilder = builder;
    }

    /**
     * 开始倒计时
     */
    public void start() {
        mBuilder.mView.setEnabled(false);
        mBuilder.mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (null == mBuilder.mView || null == mBuilder.mView.getContext()) {
                    cancel();
                    return;
                }
                if (mBuilder.mSeconds > 1) {
                    mBuilder.mView.post(new Runnable() {
                        @Override
                        public void run() {
                            mBuilder.mView.setText(mBuilder.mResendText + "（" + (--mBuilder.mSeconds) + "）");
                        }
                    });
                    return;
                }
                mBuilder.mView.post(new Runnable() {
                    @Override
                    public void run() {
                        mBuilder.mView.setText(mBuilder.mResendText);
                        mBuilder.mView.setEnabled(true);
                    }
                });
                cancel();
            }
        }, 0, 1000L);
    }

    /**
     * 获取计时器
     *
     * @param view 显示倒计时的控件
     * @return 计时器
     */
    public static CaptchaDownHelper get(TextView view) {
        return new CaptchaDownHelper.Builder().view(view).build();
    }

    /**
     * 获取计时器
     *
     * @param view    显示倒计时的控件
     * @param seconds 倒计时总时间，单位s
     * @return 计时器
     */
    public static CaptchaDownHelper get(TextView view, int seconds) {
        return new CaptchaDownHelper.Builder().view(view).seconds(seconds).build();
    }

    /**
     * 获取计时器
     *
     * @param view       显示倒计时的控件
     * @param resendText 重新发送按钮的文字，默认【重新发送】
     * @return 计时器
     */
    public static CaptchaDownHelper get(TextView view, String resendText) {
        return new CaptchaDownHelper.Builder().view(view).resendText(resendText).build();
    }

    /**
     * 获取计时器
     *
     * @param view       显示倒计时的控件
     * @param seconds    倒计时总时间，单位s
     * @param resendText 重新发送按钮的文字，默认【重新发送】
     * @return 计时器
     */
    public static CaptchaDownHelper get(TextView view, int seconds, String resendText) {
        return new CaptchaDownHelper.Builder().view(view).seconds(seconds).resendText(resendText).build();
    }

    /**
     * 构造器
     */
    public static class Builder {
        // 计时器
        private Timer mTimer;
        // 倒计时总秒数
        private int mSeconds;
        // 发送验证码按钮
        private TextView mView;
        // 重新发送按钮文字
        private String mResendText;

        /**
         * 构造方法
         */
        public Builder() {
            mTimer = new Timer();
            mSeconds = 60;
            mResendText = "重新发送";
        }

        /**
         * 设置秒数
         *
         * @param seconds 秒数
         * @return 构造器
         */
        public Builder seconds(int seconds) {
            this.mSeconds = seconds;
            return this;
        }

        /**
         * 设置显示倒计时的控件
         *
         * @param view 显示倒计时的控件
         * @return 构造器
         */
        public Builder view(TextView view) {
            this.mView = view;
            return this;
        }

        /**
         * 设置重新发送按钮的文字
         *
         * @param text 重新发送按钮的文字
         * @return 构造器
         */
        public Builder resendText(String text) {
            this.mResendText = text;
            return this;
        }

        /**
         * 创建计时器
         *
         * @return 计时器实例
         */
        public CaptchaDownHelper build() {
            return new CaptchaDownHelper(this);
        }
    }
}
