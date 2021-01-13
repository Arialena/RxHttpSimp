package com.wy.arialena.modulecore.utils;

import android.text.TextUtils;

/**
 * @author wuyan
 * 类型转换
 */
public class NumberConversionUtil {

    /**
     * int
     * @param f
     * @return
     */
    public static String intToStr(int f){
        String s = Integer.toString(f);
        return s;
    }

    /**
     * float转为string
     * @param f
     * @return
     */
    public static String floatToStr(float f){
        String s = Float.toString(f);
        return s;
    }

    /**
     * double转为string
     * @param f
     * @return
     */
    public static String doubleToStr(double f){
        String s = Double.toString(f);
        return s;
    }

    /**
     *字符串转int
     * @param s
     * @return
     */
    public static int strToInt(String s){
        try {
            return Integer.valueOf(s).intValue();
        }catch (Exception e){
            return 0;
        }
    }

    /**
     *字符串转float
     * @param s
     * @return
     */
    public static float strToFloat(String s){
        try {
            return Float.valueOf(s).floatValue();
        }catch (Exception e){
            return 0f;
        }
    }

    /**
     *字符串转double
     * @param s
     * @return
     */
    public static double strToDouble(String s){
        try {
            return Double.valueOf(s).doubleValue();
        }catch (Exception e){
            return 0;
        }
    }

    /**
     * int转float
     * @param number
     * @return
     */
    public static float intToFloat(int number){
        return strToInt(intToStr(number));
    }

    /**
     * int转double
     * @param number
     * @return
     */
    public static double intToDouble(int number){
        return strToDouble(intToStr(number));
    }

    /**
     * float转double
     * @param number
     * @return
     */
    public static double floatToDouble(float number){
        return strToDouble(floatToStr(number));
    }

    //把String转化为float
    public static float convertToFloat(String number) {
        if (TextUtils.isEmpty(number)) {
            return 0f;
        }
        try {
            return Float.parseFloat(number);
        } catch (Exception e) {
            return 0f;
        }

    }

    //把String转化为double
    public static double convertToDouble(String number) {
        if (TextUtils.isEmpty(number)) {
            return 0;
        }
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            return 0;
        }

    }

    //把String转化为int
    public static int convertToInt(String number) {
        if (TextUtils.isEmpty(number)) {
            return 0;
        }
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            return 0;
        }
    }
}
