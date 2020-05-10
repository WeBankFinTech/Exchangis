/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by devendeng on 2018/10/22.
 */
public class DateTool {
    private static final String TIME_PLACEHOLDER_DATE_0 = "${yyyyMMdd}";
    private static final String TIME_PLACEHOLDER_DATE_1 = "${yyyy-MM-dd}";
    public static final String TIME_PLACEHOLDER_DATE_TIME = "${yyyy-MM-dd HH:mm:ss}";
    private static final String TIME_PLACEHOLDER_TIMESTAMP = "${timestamp}";
    static final String MONTH_BEGIN_SYMBOL = "run_month_begin";
    static final String MONTH_END_SYMBOL = "run_month_end";
    static final String LINE_SYMBOL = "_std";
    public static final String[] TIME_PLACEHOLDER = new String[]
            {TIME_PLACEHOLDER_DATE_0, TIME_PLACEHOLDER_DATE_1, TIME_PLACEHOLDER_DATE_TIME, TIME_PLACEHOLDER_TIMESTAMP};

    private static final String TIME_REGULAR_EXPRESSION = "\\$\\{(run_date|run_month_begin|run_month_end)(_std)?\\s*([+-])?\\s*([0-9])?\\}";
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
