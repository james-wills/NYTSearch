package com.example.james_wills.nytsearch.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by james_wills on 5/27/16.
 */
public class DateFormatUtils {
  public static final SimpleDateFormat USER_FORMAT = new SimpleDateFormat("MM/dd/yy");
  public static final SimpleDateFormat RESPONSE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  public static final SimpleDateFormat PARAM_FORMAT = new SimpleDateFormat("yyyyMMdd");

  public static String convert(String date, SimpleDateFormat startFormat, SimpleDateFormat endFormat) throws ParseException{
    return endFormat.format(startFormat.parse(date));
  }

  public static Calendar getCalendar(String dateString, SimpleDateFormat format) throws ParseException {
    Date date = format.parse(dateString);
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c;
  }
}
