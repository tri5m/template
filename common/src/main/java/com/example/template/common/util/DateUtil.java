package com.example.template.common.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Title:日期工具类
 *
 * @author: trifolium.wang
 * @date: 2024/9/24
 */
public class DateUtil {
    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);
    public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前系统日期
     *
     * @return 系统日期yyyy-MM-dd
     */
    public static String getDateStr() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date());
    }

    /**
     * 获取当前系统日期
     *
     * @return 系统日期yyyy-MM-dd
     */
    public static String getDateStr(Date date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static Date getDate(String dateStr, String formatStr) {
        if (StrUtil.isEmpty(dateStr)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static boolean isDate(String dateStr) {
        if (getDate(dateStr, YMD_HMS) == null) {
            if (getDate(dateStr, "yyyy-MM-dd") == null) {
                return getDate(dateStr, "yyyyMMddHHmmss") != null;
            }
        }

        return true;
    }

    /**
     * 获取一个时间字符串的毫秒
     */
    public static Long getMillisByString(String str) {
        if (StrUtil.isEmpty(str)) {
            return System.currentTimeMillis();
        }

        return cn.hutool.core.date.DateUtil.parse(str).getTime();
    }

    /**
     * 将日期时间转为日期
     */
    public static String dateTimeConvertDate(String dateTimeStr) {
        if (StrUtil.isEmpty(dateTimeStr)) {

            return dateTimeStr;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat fromFormat = new SimpleDateFormat(YMD_HMS);
        try {
            return format.format(fromFormat.parse(dateTimeStr));
        } catch (ParseException e) {

            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将日期时间转为时分秒
     */
    public static String dateTimeConvertTime(String dateTimeStr) {
        if (StrUtil.isEmpty(dateTimeStr)) {

            return dateTimeStr;
        }
        DateFormat format = new SimpleDateFormat("HH:mm:ss");
        DateFormat fromFormat = new SimpleDateFormat(YMD_HMS);
        try {
            return format.format(fromFormat.parse(dateTimeStr));
        } catch (ParseException e) {

            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // 获取小时
    public static String getHoursStr(Date date) {
        DateFormat format = new SimpleDateFormat("HH");
        return format.format(date);
    }

    /**
     * 获取当前系统日期时间
     *
     * @return 系统日期时间yyyy-MM-dd hh:mm:ss
     */
    public static String getDatetimeStr() {
        return getDatetimeStr(YMD_HMS);
    }

    /**
     * 获取当前系统日期时间
     *
     * @return 系统日期时间yyyy-MM-dd hh:mm:ss
     */
    public static String getDatetimeStr(Date date) {
        if (date == null) {

            return null;
        }
        DateFormat format = new SimpleDateFormat(YMD_HMS);
        return format.format(date);
    }

    /**
     * 获取当前系统日期时间
     *
     * @return 系统日期时间
     */
    public static String getDatetimeStr(String format) {
        DateFormat ss = new SimpleDateFormat(format);
        return ss.format(new Date());
    }

    /**
     * 获取时间戳
     *
     * @return 时间戳字符串yyyyMMddHHmmss
     */
    public static String getTimestamp() {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date());
    }

    /**
     * 获取时间差
     *
     * @param starTime 开始时间 格式(yyyy-MM-dd hh:mm:ss)
     * @param endTime  结束时间 格式(yyyy-MM-dd hh:mm:ss)
     * @return 秒
     */
    public static long getTimePoor(String starTime, String endTime) throws ParseException {
        SimpleDateFormat dfs = new SimpleDateFormat(YMD_HMS);
        Date begin = dfs.parse(starTime);
        Date end = dfs.parse(endTime);
        //除以1000是为了转换成秒
        return (end.getTime() - begin.getTime()) / 1000;
    }

    public static long offsetSecond(long starTime, long endTime) {

        return (endTime - starTime) / 1000;
    }

    /**
     * 获取时间差天数
     **/
    public static int differentDays(Date startDate, Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24));
    }

    /**
     * 获取年龄精确到天
     */
    public static String ageDayString(Date startDate) {
        if (startDate != null) {
            Integer dayInt = DateUtil.differentDays(startDate, new Date());
            int year = dayInt / 365;
            int mon = dayInt % 365;
            if (dayInt > 31) {
                return (year > 0 ? year + "岁" : "") + ((mon > 31 ? mon / 31 + "个月" : "1个月"));
            } else {
                return dayInt + "天";
            }
        } else {
            return "";
        }
    }

    /**
     * 根据日期字符串获取Calendar对象
     *
     * @param date   日期字符串
     * @param format 日期格式
     * @return Calendar对象(java中获取的月份从0开始)
     */
    public static Calendar getCalendar(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (!StrUtil.isEmpty(date)) {
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(date));
                return c;
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    /**
     * 获取{@param day}天后或前 日期
     *
     * @param date 指定一个时间
     * @param day  正数为 day 天后， 负数为day天前
     * @return day天后或前的日期
     */
    public static Date getSpecifyDayTime(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }


    public static Date beginOfDay() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    public static Date endOfDay() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    public static String convertTimeToFormat(long timeStamp) {
        long curTime = System.currentTimeMillis() / (long) 1000;
        long time = curTime - timeStamp;

        if (time < 60 && time >= 0) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 7) {
            return time / 3600 / 24 + "天前";
        } else if (time >= 3600 * 24 * 7) {
            return "7天前";
        } else {
            return "刚刚";
        }
    }

    /**
     * 将秒转换为时分秒
     */
    public static String secToHourMinuteSeconds(Date startTime, Date endTime) {

        if (startTime == null) {
            throw new RuntimeException("start time is not null.");
        }
        if (endTime == null) {
            endTime = new Date();
        }

        if (endTime.getTime() < startTime.getTime()) {
            throw new RuntimeException("start time is lg end time.");
        }

        long hours = cn.hutool.core.date.DateUtil.between(startTime, endTime, DateUnit.HOUR);

        long minute = cn.hutool.core.date.DateUtil.between(
                cn.hutool.core.date.DateUtil.offsetHour(startTime, (int) hours),
                endTime, DateUnit.MINUTE);

        long second = cn.hutool.core.date.DateUtil.between(
                cn.hutool.core.date.DateUtil.offsetMinute(cn.hutool.core.date.DateUtil.offsetHour(startTime,
                        (int) hours), (int) minute),
                endTime, DateUnit.SECOND);

        return (hours != 0 ? (hours + "时") : "")
                + (minute != 0 ? (minute + "分") : "")
                + (second != 0 ? (second + "秒") : "");
    }

    /**
     * 时间差分钟，想上取整
     */
    public static String betweenToMinuteRoundUp(Date startTime, Date endTime) {
        if (startTime == null) {
            throw new RuntimeException("start time is not null.");
        }
        if (endTime == null) {
            endTime = new Date();
        }

        long minute = cn.hutool.core.date.DateUtil.between(startTime, endTime, DateUnit.MINUTE);

        if (Math.abs(endTime.getTime() - startTime.getTime()) % (1000 * 60) != 0) {

            return (minute + 1) + "分钟";
        }

        return minute + "分钟";
    }

}
