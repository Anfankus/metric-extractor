package cn.edu.seu.java.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeMonitor {

  private long startTime;

  public void start() {
    this.startTime = System.currentTimeMillis();
  }

  public void end() {
    long during = System.currentTimeMillis() - this.startTime;
    long hour = during / 3600000;
    long min = (during % 3600000) / 60000;
    long sec = (during % 60000) / 1000;
    long minsec = during % 1000;
    System.out.println("Running time is " + hour + ":" + min + ":" + sec + ":" + minsec);
  }

  public static String dateAndTime() {
    Date date = new Date();
    DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return df1.format(date).toString();
  }
}