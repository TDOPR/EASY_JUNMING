package com.haoliang.common.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {
    public static final String SIMPLE_FORMAT = "yyyyMMdd";
    public static final String SIMPLE_FORMAT_LINE = "yyyy-MM-dd";
    public static final String SIMPLE_FORMAT_PATH = "yyyy/MM/dd";
    public static final String DETAIL_FORMAT = "yyyy年MM月dd日 HH:mm:ss";
    public static final String DETAIL_FORMAT_LINE = "yyyy-MM-dd HH:mm:ss";
    public static final String DETAIL_FORMAT_NO_UNIT = "yyyyMMddHHmmssSSS";

    /***
     * 获取延时MINUTES分钟后的时间
     * @param minutes
     * @return
     */
    public static Date getDelayMinutes(int minutes) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, minutes);
        return c.getTime();
    }

    /***
     * 格式化日期
     * @param myDate
     * @param fromatString
     * @return
     */
    public static String formatDate(Date myDate, String fromatString) {
        SimpleDateFormat myFormat = new SimpleDateFormat(fromatString);
        return myFormat.format(myDate);
    }

    /**
     * 把日期字符转换成时间戳
     */
    public static long parseDateStr(String dateStr) {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM");
        try {
            Date date = myFormat.parse(dateStr);
            return date.getTime();
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 把日期字符转换成时间戳
     */
    public static Date parseDate(String dateStr, String format) {
        SimpleDateFormat myFormat = new SimpleDateFormat(format);
        try {
            Date date = myFormat.parse(dateStr);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    /***
     * 生成详细日期
     * @return
     */
    public static String getDetailTime() {
        SimpleDateFormat myFormat = new SimpleDateFormat(DETAIL_FORMAT);
        return myFormat.format(new Date());
    }

    /***
     * 生成详细日期，不要单位。格式YYMMDDhhmmss
     * @return
     */
    public static String getDetailTimeIgnoreUnit() {
        SimpleDateFormat myFormat = new SimpleDateFormat(DETAIL_FORMAT_NO_UNIT);
        return myFormat.format(new Date());
    }

    public static String getSimpleDate() {
        SimpleDateFormat myFormat = new SimpleDateFormat(SIMPLE_FORMAT);
        return myFormat.format(new Date());
    }

    /**
     * 获取当前日期加一天的时间字符串
     */
    public static String getDateStrIncrement(String dateStr) {
        String result;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(dateStr));//设置起时间
            //cal.add(Calendar.YEAR, 1);//增加一年
            cal.add(Calendar.DATE, 1);//增加一天
            //cal.add(Calendar.DATE, -10);//减10天
            //cal.add(Calendar.MONTH, n);//增加一个月
            result = sdf.format(cal.getTime());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取当前日期增加指定时间的格式化字符
     *
     * @param date     原始时间
     * @param time     增加的数值
     * @param timeUnit 单位
     * @return
     */
    public static LocalDateTime getDateStrIncrement(LocalDateTime date, Integer time, TimeUnit timeUnit) {
        LocalDateTime newDate = LocalDateTime.now();
        switch (timeUnit) {
            case SECONDS:
                newDate = date.plusSeconds(time);
                break;
            case MINUTES:
                //增加分钟
                newDate = date.plusMinutes(time);
                break;
            case HOURS:
                //增加小时
                newDate = date.plusHours(time);
                break;
            case DAYS:
                //增加天
                newDate = date.plusDays(time);
                break;
        }
        return newDate;
    }


    public static Date getDateIncrement(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(dateStr));//设置起时间
            cal.add(Calendar.DATE, 1);//增加一天
            return cal.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 检查日期是否失效了
     */
    public static boolean checkIsInvalid(Date dealine) {
        Date nowDate = new Date();
        if (dealine.getTime() < nowDate.getTime()) {
            return true;
        }
        return false;
    }

    /**
     * 获取今日的时间
     *
     * @return
     */
    public static LocalDate getNowDateNotHours() {
        return LocalDate.now();
    }

    public static void main(String[] args) {
        System.out.println(getNowDateNotHours());
    }


    /**
     * 计算两个日期之前相差的月数
     *
     * @param startLocalDate 开始日期
     * @param endLocalDate   结束日期
     * @return
     */
    public static int betweenMonths(LocalDate startLocalDate, LocalDate endLocalDate) {
        LocalDate start = LocalDate.of(startLocalDate.getYear(), startLocalDate.getMonth(), 1);
        LocalDate end = LocalDate.of(endLocalDate.getYear(), endLocalDate.getMonth(), 1);
        return (int) ChronoUnit.MONTHS.between(start, end);
    }

    public static String getMonthEnglish(Integer month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sept";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "";
        }
    }

}
