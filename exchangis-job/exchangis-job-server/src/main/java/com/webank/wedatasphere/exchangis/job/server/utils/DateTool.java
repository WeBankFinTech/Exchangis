package com.webank.wedatasphere.exchangis.job.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by devendeng on 2018/10/22.
 */
public class DateTool {
    private static final String TIME_PLACEHOLDER_DATE_0 = "${yyyyMMdd}";
    private static final String TIME_PLACEHOLDER_DATE_1 = "${yyyy-MM-dd}";
    public static final String TIME_PLACEHOLDER_DATE_TIME = "${yyyy-MM-dd HH:mm:ss}";
    /**
     * yyyyMMddHH
     * yyyy-MM-dd-HH
     * HH
     */
    public static final String TIME_PLACEHOLDER_DATE_HOUR_0 = "${yyyyMMddHH}";
    public static final String TIME_PLACEHOLDER_DATE_HOUR_1 = "${yyyy-MM-dd-HH}";
    public static final String TIME_PLACEHOLDER_DATE_HOUR_2 = "${HH}";

    static final String TIME_PLACEHOLDER_TIMESTAMP = "timestamp";
    static final String MONTH_BEGIN_SYMBOL = "run_month_begin";
    static final String MONTH_END_SYMBOL = "run_month_end";
    static final String[] HOUR_SPEC_SYMBOLS = new String[]{"yyyyMMdd", "yyyy-MM-dd", "HH"};
    static final String FORMAT_STD_SYMBOL = "_std";
    static final String FORMAT_UTC_SYMBOL = "_utc";
    public static final String[] TIME_PLACEHOLDER = new String[]{
            TIME_PLACEHOLDER_DATE_0, TIME_PLACEHOLDER_DATE_1, TIME_PLACEHOLDER_DATE_TIME, TIME_PLACEHOLDER_TIMESTAMP,
            TIME_PLACEHOLDER_DATE_HOUR_0, TIME_PLACEHOLDER_DATE_HOUR_1, TIME_PLACEHOLDER_DATE_HOUR_2};

    private static final String TIME_REGULAR_EXPRESSION = "\\$\\{(run_date|run_month_begin|run_month_end|HH|yyyyMMddHH|yyyy-MM-dd-HH|yyyyMMdd|yyyy-MM-dd|timestamp)(_std|_utc|_y|_M|_d)?\\s*([+-])?\\s*([0-9])?\\}";
    public static final Pattern TIME_REGULAR_PATTERN = Pattern.compile(TIME_REGULAR_EXPRESSION);

    private static Logger log = LoggerFactory.getLogger(DateTool.class);
    private Calendar calendar=Calendar.getInstance();

    public DateTool(Date date){
        this.calendar.setTime(date);
    }

    public DateTool(long timeInMillis){
        this.calendar.setTimeInMillis(timeInMillis);
    }

    public DateTool(){
    }

    public DateTool set(int field, int value) {
        calendar.set(field, value);
        return this;
    }

    public DateTool getDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            log.error("Parse exception.",e);
        }
        return this;
    }

    /**
     *
     * @return
     */
    public DateTool getHalfBeg(int amount) {
        calendar.add(Calendar.MONTH, amount * 6);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        if (currentMonth >= 1 && currentMonth <= 6) {
            calendar.set(Calendar.MONTH, 0);
        } else if (currentMonth >= 7 && currentMonth <= 12) {
            calendar.set(Calendar.MONTH, 6);
        }
        calendar.set(Calendar.DATE, 1);
        return this;
    }

    /**
     *
     * @return
     */
    public DateTool getHalfEnd(int amount) {
        calendar.add(Calendar.MONTH, amount * 6);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        if (currentMonth >= 1 && currentMonth <= 6) {
            calendar.set(Calendar.MONTH, 5);
            calendar.set(Calendar.DATE, 30);
        } else if (currentMonth >= 7 && currentMonth <= 12) {
            calendar.set(Calendar.MONTH, 11);
            calendar.set(Calendar.DATE, 31);
        }
        return this;
    }

    /**
     * Begin of quarter
     *
     * @return
     */
    public DateTool getQuarterBeg(int amount) {
        calendar.add(Calendar.MONTH, amount * 3);

        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        if (currentMonth >= 1 && currentMonth <= 3) {
            calendar.set(Calendar.MONTH, 0);
        } else if (currentMonth >= 4 && currentMonth <= 6) {
            calendar.set(Calendar.MONTH, 3);
        } else if (currentMonth >= 7 && currentMonth <= 9) {
            calendar.set(Calendar.MONTH, 6);
        } else if (currentMonth >= 10 && currentMonth <= 12) {
            calendar.set(Calendar.MONTH, 9);
        }
        calendar.set(Calendar.DATE, 1);
        return this;
    }

    /**
     * End of quarter
     *
     * @return
     */
    public DateTool getQuarterEnd(int amount) {
        calendar.add(Calendar.MONTH, amount * 3);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        if (currentMonth >= 1 && currentMonth <= 3) {
            calendar.set(Calendar.MONTH, 2);
            calendar.set(Calendar.DATE, 31);
        } else if (currentMonth >= 4 && currentMonth <= 6) {
            calendar.set(Calendar.MONTH, 5);
            calendar.set(Calendar.DATE, 30);
        } else if (currentMonth >= 7 && currentMonth <= 9) {
            calendar.set(Calendar.MONTH, 8);
            calendar.set(Calendar.DATE, 30);
        } else if (currentMonth >= 10 && currentMonth <= 12) {
            calendar.set(Calendar.MONTH, 11);
            calendar.set(Calendar.DATE, 31);
        }
        return this;
    }

    public DateTool addDay(int amount){
        calendar.add(Calendar.DAY_OF_YEAR, amount);
        return this;
    }

    public DateTool addHour(int amount){
        calendar.add(Calendar.HOUR_OF_DAY, amount);
        return this;
    }
    public DateTool add(int field, int amount){
        calendar.add(field, amount);
        return this;
    }

    public DateTool addMonth(int amount){
        calendar.add(Calendar.MONTH, amount);
        return this;
    }

    public DateTool addYesterdayMonth(int amount){
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.add(Calendar.MONTH, amount);
        return this;
    }
    public DateTool getMonthEnd(int amount){
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.MONTH, amount+1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return this;
    }

    public DateTool getMonthBegin(int amount){
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.MONTH, amount);
        return this;
    }

    public String format(String pattern){
        SimpleDateFormat format=new SimpleDateFormat(pattern);
        return format.format(calendar.getTime());
    }
    public String format(String pattern, String timeZone){
        SimpleDateFormat format=new SimpleDateFormat(pattern);
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        return format.format(calendar.getTime());
    }
    public String format(String pattern, long time){
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(time));
    }
    public String currentTimestamp(){
        return String.valueOf(System.currentTimeMillis()/1000);
    }


    public static Date stringToDate(String dateStr, String formatStr){
        DateFormat sdf=new SimpleDateFormat(formatStr);
        Date date=null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            log.error("Parse exception.",e);
        }
        return date;
    }

    public DateTool truncate(long mills) {
        calendar.setTimeInMillis(mills * (calendar.getTimeInMillis() / mills));
        return this;
    }

}
