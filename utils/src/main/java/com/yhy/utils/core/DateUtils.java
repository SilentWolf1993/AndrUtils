package com.yhy.utils.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2017-10-16 10:00
 * version: 1.0.0
 * desc   : 日期时间工具类
 */
public class DateUtils {
    public static final String FORMAT_ALL = "yyyy年MM月dd日 HH:mm:ss";
    public static final String FORMAT_DATE = "yyyy年MM月dd日";
    public static final String FORMAT_TIME = "HH:mm:ss";

    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    private static final long MONTH = 30 * DAY;

    private DateUtils() {
        throw new UnsupportedOperationException("Can not create instance for class DateUtils");
    }

    /**
     * 以默认格式格式化时间
     *
     * @param millions 毫秒数
     * @return 时间字符串
     */
    public static String formatDateTime(long millions) {
        return formatDateTime(millions, FORMAT_ALL);
    }

    /**
     * 自定义格式化时间
     *
     * @param millions 毫秒数
     * @param pattern  格式
     * @return 时间字符串
     */
    public static String formatDateTime(long millions, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(new Date(millions));
    }

    /**
     * 解析字符串类型的日期
     *
     * @param dateStr 日期字符串
     * @return 毫秒值
     */
    public static long parseDateTime(String dateStr) {
        return parseDateTime(dateStr, FORMAT_ALL);
    }

    /**
     * 解析字符串类型的日期
     *
     * @param dateStr 日期字符串
     * @param pattern 格式
     * @return 毫秒值
     */
    public static long parseDateTime(String dateStr, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            return format.parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断是否是同一天
     *
     * @param dateA 第一个时间日期
     * @param dateB 第二个时间日期
     * @return 是否是同一天
     */
    public static boolean isSameDay(Date dateA, Date dateB) {
        Calendar calA = Calendar.getInstance();
        calA.setTime(dateA);
        Calendar calB = Calendar.getInstance();
        calB.setTime(dateB);
        return isSameDay(calA, calB);
    }

    /**
     * 判断是否是同一天
     *
     * @param calA 第一个时间日期
     * @param calB 第二个时间日期
     * @return 是否是同一天
     */
    public static boolean isSameDay(Calendar calA, Calendar calB) {
        return calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) && calA.get(Calendar.MONTH) == calB.get(Calendar.MONTH) && calA.get(Calendar.DAY_OF_MONTH) == calB.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 以友好的方式显示时间
     *
     * @param date 时间
     * @return 格式化结果
     */
    public static String friendlyDate(Date date) {
        if (null == date) {
            return "";
        }
        Calendar calA = Calendar.getInstance();
        calA.setTime(date);

        return friendlyDate(calA);
    }

    /**
     * 以友好的方式显示时间
     *
     * @param cale 时间
     * @return 格式化结果
     */
    public static String friendlyDate(Calendar cale) {
        if (null == cale) {
            return "";
        }
        return friendlyDate(cale.getTimeInMillis());
    }


    /**
     * 以友好的方式显示时间
     *
     * @param millis 毫秒数
     * @return 格式化结果
     */
    public static String friendlyDate(long millis) {
        Calendar now = Calendar.getInstance();
        //计算时间差
        long deltaTime = now.getTimeInMillis() - millis;

        //20s以内
        if (deltaTime < 20 * SECOND) {
            // 20s以内
            return "刚刚";
        }

        //一分钟以内
        if (deltaTime < MINUTE) {
            int seconds = (int) (deltaTime / SECOND);
            return seconds + "秒前";
        }

        //一小时以内
        if (deltaTime < HOUR) {
            int minutes = (int) (deltaTime / MINUTE);
            return minutes + "分钟前";
        }

        //一天以内
        if (deltaTime < DAY) {
            int hours = (int) (deltaTime / HOUR);
            return hours + "小时前";
        }

        //一个月以内
        if (deltaTime < MONTH) {
            int days = (int) (deltaTime / DAY);
            if (days == 1) {
                return "昨天";
            } else if (days == 2) {
                return "前天";
            }
            return days + "天前";
        }

        //标准形式显示时间
        return formatDateTime(millis);
    }
}
