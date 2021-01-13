package com.wy.arialena.modulecore.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * @author wuyan
 * 日期转换工具
 */
public class DateUtils {

    /**
     * 根据年份及月份计算每月的天数
     */
    public static int calculateDaysInMonth(int year, int month) {
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] bigMonths = {"1", "3", "5", "7", "8", "10", "12"};
        String[] littleMonths = {"4", "6", "9", "11"};
        List<String> bigList = Arrays.asList(bigMonths);
        List<String> littleList = Arrays.asList(littleMonths);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (bigList.contains(String.valueOf(month))) {
            return 31;
        } else if (littleList.contains(String.valueOf(month))) {
            return 30;
        } else {
            if (year <= 0) {
                return 29;
            }
            // 是否闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                return 29;
            } else {
                return 28;
            }
        }
    }

    /**
     * 月日时分秒，0-9前补0
     */
    @NonNull
    public static String fillZero(int number) {
        return number < 10 ? "0" + number : "" + number;
    }

    /**
     * 截取掉前缀0以便转换为整数
     *
     * @see #fillZero(int)
     */
    public static int trimZero(@NonNull String text) {
        try {
            if (text.startsWith("0")) {
                text = text.substring(1);
            }
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 获取年
     * @return
     */
    public static int getYear(){
        Calendar cd = Calendar.getInstance();
        return  cd.get(Calendar.YEAR);
    }
    /**
     * 获取月
     * @return
     */
    public static int getMonth(){
        Calendar cd = Calendar.getInstance();
        return  cd.get(Calendar.MONTH)+1;
    }
    /**
     * 获取日
     * @return
     */
    public static int getDay(){
        Calendar cd = Calendar.getInstance();
        return  cd.get(Calendar.DATE);
    }
    /**
     * 获取时
     * @return
     */
    public static int getHour(){
        Calendar cd = Calendar.getInstance();
        return  cd.get(Calendar.HOUR);
    }
    /**
     * 获取分
     * @return
     */
    public static int getMinute() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.MINUTE);
    }

    /**
     * 年份获取
     * @return
     */
    public static List<String> getYears(){
        int year = getYear();
        List<String> yList = new ArrayList<>();
        for (int i = 2020; i <= year; i++){
            yList.add(i + "");
        }
        return yList;
    }

    /**
     * 月份获取
     * @return
     */
    public static List<String> getMonths(){
        int month = getMonth();
        List<String> monthList = new ArrayList<>();
        for (int initMonth = 1; initMonth <= month; initMonth++){
            monthList.add(initMonth + "");
        }

        return monthList;
    }
}
