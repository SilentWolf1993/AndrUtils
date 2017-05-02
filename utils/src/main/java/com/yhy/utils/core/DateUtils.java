package com.yhy.utils.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by HongYi Yan on 2017/3/23 11:43.
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
        throw new RuntimeException("Can not create instance for class DateUtils");
    }

    public static String formatDateTime(long millions) {
        return formatDateTime(millions, FORMAT_ALL);
    }

    public static String formatDateTime(long millions, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(millions));
    }

    public static boolean isSameDay(Date dateA, Date dateB) {
        Calendar calA = Calendar.getInstance();
        calA.setTime(dateA);
        Calendar calB = Calendar.getInstance();
        calB.setTime(dateB);
        return isSameDay(calA, calB);
    }

    public static boolean isSameDay(Calendar calA, Calendar calB) {
        return calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) && calA.get(Calendar.MONTH) == calB.get(Calendar.MONTH) && calA.get(Calendar.DAY_OF_MONTH) == calB.get(Calendar.DAY_OF_MONTH);
    }

    public static String friendlyDate(Date date) {
        if (null == date) {
            return "";
        }
        Calendar calA = Calendar.getInstance();
        calA.setTime(date);

        return friendlyDate(calA);
    }

    public static String friendlyDate(Calendar date) {
        if (null == date) {
            return "";
        }
        return friendlyDate(date.getTimeInMillis());
    }

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

//    private static boolean isPrevDay(Calendar date, Calendar now) {
//        //到当前0点的日期
//        Calendar temp = Calendar.getInstance();
//        temp.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
//
//        //如果参数日期时间减去当前0点时间小于0，说明是前一天的日期
//        return date.getTimeInMillis() - temp.getTimeInMillis() < 0;
//    }
}
