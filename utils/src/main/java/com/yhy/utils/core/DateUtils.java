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
}
