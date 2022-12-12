package com.haoliang.common.util;

import com.haoliang.common.model.ThreadLocalManager;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SIMPLE_DATETIME_FORMAT = "yyyy-MM-dd HH:ss";
    public static final String DETAIL_FORMAT_NO_UNIT = "yyyyMMddHHmmssSSS";

    private static final String LANGUAGE_KEY_PREFIX = "month.";

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


    public static Date parseDate(String dateStr, String format) {
        SimpleDateFormat myFormat = new SimpleDateFormat(format);
        try {
            Date date = myFormat.parse(dateStr);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatDate(Date date, String format) {
        SimpleDateFormat myFormat = new SimpleDateFormat(format);
        return myFormat.format(date);
    }

    /**
     * 把LocalDate转成
     */
    public static Date getNowDate() {
//        ZoneId zone = ZoneId.systemDefault();
//        Instant instant = LocalDate.now().atStartOfDay().atZone(zone).toInstant();
//        return Date.from(instant);
        return parseDate(LocalDate.now().toString(), SIMPLE_DATE_FORMAT);
    }

    /**
     * 把日期字符转换成日期
     */
    public static LocalDate parseLocalDate(String dateStr) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
    }

    /**
     * 把日期字符转换成日期类型
     */
    public static LocalDate parseLocalDate(String dateStr, String format) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
    }

    /**
     * 把日期字符转换成日期时间
     */
    public static LocalDateTime parseLocalDateTime(String dateStr) {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
    }

    /**
     * 把日期字符转换成日期时间
     */
    public static LocalDateTime parseLocalDateTime(String dateStr, String format) {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(format));
    }


    /***
     * 生成详细日期，不要单位。格式YYMMDDhhmmss
     * @return
     */
    public static String getDetailTimeIgnoreUnit() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DETAIL_FORMAT_NO_UNIT));
    }

    /**
     * 获取当前日期加一天的时间字符串
     */
//    public static String getDateStrIncrement(String dateStr) {
//        String result;
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(sdf.parse(dateStr));//设置起时间
//            //cal.add(Calendar.YEAR, 1);//增加一年
//            cal.add(Calendar.DATE, 1);//增加一天
//            //cal.add(Calendar.DATE, -10);//减10天
//            //cal.add(Calendar.MONTH, n);//增加一个月
//            result = sdf.format(cal.getTime());
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
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


    /**
     * 获取昨天的时间
     * @return
     */
    public static LocalDate getAverLocalDate() {
        return LocalDate.now().minusDays(1);
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
        System.out.println(getNowDate());
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

    /**
     * 根据国际化语言获取对应的月份名称
     */
    public static String getMonthStrByLanguage(Integer month) {
        String key = LANGUAGE_KEY_PREFIX + month;
        return MessageUtil.get(key, ThreadLocalManager.getLanguage());
    }


}
