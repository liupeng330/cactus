package com.qunar.corp.cactus.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Date: 13-11-11 Time: 下午12:52
 * 
 * @author: xiao.liang
 * @description:
 */
public class DateHelper {

    public static final DateTimeFormatter TO_SECOND = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static Date str2DateWithSecond(String time) {
        return TO_SECOND.parseDateTime(time).toDate();
    }
}
