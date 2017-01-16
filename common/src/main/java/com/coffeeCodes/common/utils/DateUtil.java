package com.coffeeCodes.common.utils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by perdonare on 2016/3/2.
 */
public class DateUtil {

    /**
     * 日期格式:yyyy-MM
     */
    public static final String dateMonthParrern = "yyyy-MM";
    /**
     * 日期格式：yyyyMMdd
     */
    public static final String datePattern = "yyyyMMdd";
    /**
     * 日期格式：yyMMdd
     */
    public static final String shortDatePattern = "yyMMdd";
    /**
     * 日期时间格式：yyyyMMddHHmmss
     */
    public static final String fullPattern = "yyyyMMddHHmmss";
    /**
     * 日期时间格式：yyyyMMddHHmmss
     */
    public static final String readPattern = "yyyy-MM-dd HH:mm:ss,SSS";
    /**
     * 日期时间格式：yyyyMMddHHmmss
     */
    public static final String allPattern = "yyyy-MM-dd HH:mm:ss";
    public static final String minutePattern = "yyyy-MM-dd HH:mm";
    /**
     * 日期时间格式：yyyyMM
     */
    public static final String mothPattern = "yyyyMM";

    /**
     * 日期格式：yyyyMMdd
     */
    public static final String monthDayPattern = "MM-dd";

    public static final String monthDayPlainPattern = "MMdd";

    /**
     * 日期时间格式：yyMMddHHmmss
     */
    public static final String partPattern = "yyMMddHHmmss";

    /**
     * 日期时间格式：dd
     */
    public static final String ddPattern = "dd";

    public static final String dPattern = "d";

    public static final String mmPattern = "MM";
    public static final String MPattern = "M";

    /**
     * 格式：HHmmss
     */
    public static final String timePattern = "HHmmss";

    public static final String timeSuffixStart = "000000";

    public static final String timeSuffixEnd = "235959";

    private static final String INVALID_PARAM_MSG = "The payDate could not be null!";

    /**
     * 时间全格式
     */
    public static final String ALL_PATTERN = "yyyyMMddHHmmssSSS";

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static Date getCurrentDate() {
        return DateTime.now().toDate();
    }

    /**
     * 获取当前时间 格式： yyyyMMddHHmmss
     *
     * @return 字符日期 格式：yyyyMMddHHmmss
     */
    public static String getCurrent() {
        return getCurrent(fullPattern);
    }

    /**
     * 获取当前时间 格式： 自定义
     *
     * @param pattern 时间格式
     * @return 自定义格式的当前时间
     */
    public static String getCurrent(String pattern) {
        return DateTime.now().toString(pattern);
    }

    /**
     * 将字符串转换成固定格式时间
     *
     * @param date    日期
     * @param pattern 自定义格式
     * @return 转换后日期
     */
    public static Date parse(String date, String pattern) {
        DateTime dateTime = parseTime(date, pattern);
        if (dateTime == null) return null;
        return dateTime.toDate();
    }

    public static DateTime parseTime(String date, String pattern) {
        return DateTimeFormat.forPattern(pattern).parseDateTime(date);
    }


    public static String format(Date date, String pattern) {
        if (date == null) return null;
        return new DateTime(date).toString(pattern);
    }

    public static String convert(String date, String targetPattern) {
        return convert(date, fullPattern, targetPattern);
    }

    public static String convert(String date, String originPattern, String targetPattern) {
        Date originDate = parse(date, originPattern);
        return format(originDate, targetPattern);
    }

    /**
     * 获取当前时间
     *
     * @return Date
     */
    public static Date getCurrentDate(String pattern) {
        DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
        return DateTime.parse(new DateTime().toString(pattern), format).toDate();
    }

    /**
     * 根据 pattern 将 dateTime 时间进行格式化
     * <p/>
     * 用来去除时分秒，具体根据结果以 pattern 为准
     *
     * @param date payDate 时间
     * @return payDate 时间
     */
    public static Date formatToDate(Date date, String pattern) {
        if (date == null) return null;
        DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
        return DateTime.parse(new DateTime(date).toString(pattern), format).toDate();
    }

    /**
     * 日期增减，负数为减
     *
     * @param dayNum 天数
     * @return 时间
     */
    public static Date plusDays(int dayNum) {
        return new DateTime().plusDays(dayNum).toDate();
    }

    /**
     * 日期增减，负数为减
     *
     * @param dayNum 天数
     * @return 时间
     */
    public static Date plusDays(Date date, int dayNum) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, dayNum);
        return calendar.getTime();
    }

    /**
     * 日期增减，负数为减
     *
     * @param monthNum 月份
     * @return 时间
     */
    public static Date plusOrAddMonth(Date sourceDate, int monthNum) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sourceDate);
        calendar.add(Calendar.MONTH, monthNum);
        return calendar.getTime();
    }

    /**
     * 按秒偏移,根据{@code source}得到{@code seconds}秒之后的日期<Br>
     *
     * @param source  , 要求非空
     * @param seconds , 秒数,可以为负
     * @return 新创建的Date对象
     */
    public static Date addSeconds(Date source, int seconds) {
        return addDate(source, Calendar.SECOND, seconds);
    }

    private static Date addDate(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException(INVALID_PARAM_MSG);
        }

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * 判断两个日期是否在同一天
     *
     * @param day1 日期1
     * @param day2 日期2
     * @return boolean
     */
    public static boolean isSameDay(Date day1, Date day2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String ds1 = sdf.format(day1);
        String ds2 = sdf.format(day2);
        return ds1.equals(ds2);
    }

    /**
     * 判断两个日期是否在同月
     *
     * @param day1
     * @param day2
     * @return
     */
    public static boolean isSameMonth(Date day1, Date day2) {
        SimpleDateFormat sdf = new SimpleDateFormat(mothPattern);
        String ds1 = sdf.format(day1);
        String ds2 = sdf.format(day2);
        if (ds1.equals(ds2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 计算给定的两个日期的差值
     *
     * @param beforeDate
     * @param afterDate
     * @return
     */
    public static int compareDate(Date beforeDate, Date afterDate) {
        DateTime before = new DateTime(beforeDate);
        DateTime after = new DateTime(afterDate);
        Days days = Days.daysBetween(before.withTimeAtStartOfDay(), after.withTimeAtStartOfDay());
        return days.getDays();
    }

    /**
     * 计算给定的两个日期相差的月份
     *
     * @param beforeDate
     * @param afterDate
     * @return
     */
    public static int calMonthDiff(Date beforeDate, Date afterDate) {
        Calendar cal1 = new GregorianCalendar();
        cal1.setTime(beforeDate);
        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(afterDate);
        return (cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR)) * 12 + cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
    }

    /**
     * 获取某个月的第一天
     */
    public static Date getFirstDayOfMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, (month - 1));
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 是否在当前时间之前
     *
     * @param dateTime
     * @return
     */
    public static boolean isBeforeCurrentTime(Date dateTime) {
        return getCurrentDate().compareTo(dateTime) > 0;
    }

    /**
     * 是否在当前时间之后
     *
     * @param dateTime
     * @return
     */
    public static boolean isAfterCurrentTime(Date dateTime) {
        return getCurrentDate().compareTo(dateTime) <0;
    }

    public static int daysBetween(Date beforeDate, Date afterDate) {
        DateTime before = new DateTime(beforeDate);
        DateTime after = new DateTime(afterDate);
        Days days = Days.daysBetween(before.withTimeAtStartOfDay(), after.withTimeAtStartOfDay());
        return days.getDays();
    }

    public static int getDay(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.getDayOfMonth();
    }

    public static int getLastDay(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.dayOfMonth().getMaximumValue();
    }

    public static Date getFirstDate(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.dayOfMonth().withMinimumValue().toDate();

    }

    public static Date getLastDate(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.dayOfMonth().withMaximumValue().toDate();
    }

    public static boolean isBeforeOrEqualseCurrentDay(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = simpleDateFormat.format(date);
        String currentDate = simpleDateFormat.format(new Date());
        return Integer.valueOf(dateStr) <= Integer.valueOf(currentDate);
    }

    public static boolean isAfterOrEqualseCurrentDay(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = simpleDateFormat.format(date);
        String currentDate = simpleDateFormat.format(new Date());
        return Integer.valueOf(dateStr) >= Integer.valueOf(currentDate);
    }

    /**
     * 当前时间是否在{@code dateTime}之后
     *
     * @param dateTime 时间
     * @return 当前时间是否在{@code dateTime}之前
     */
    public static boolean isAfter(Date dateTime) {
        return new DateTime().isAfter(dateTime.getTime());
    }

}

