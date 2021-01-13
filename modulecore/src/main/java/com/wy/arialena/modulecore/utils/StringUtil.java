package com.wy.arialena.modulecore.utils;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
/**
 * @author wuyan
 * 字符串工具类
 */
public class StringUtil {

    public static String getStringNum(String s) {
        String regex = "[^0-9]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        return matcher.replaceAll("").trim();
    }

    /**
     * @Description 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim()) && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param str       字符串只能为两位小数或者整数
     * @param isDecimal 是否是小数
     * @Description 格式化字符串，每三位用逗号隔开
     */
    public static String addComma(String str, boolean isDecimal) {
        //先将字符串颠倒顺序
        str = new StringBuilder(str).reverse().toString();
        if (str.equals("0")) {
            return str;
        }
        String str2 = "";
        for (int i = 0; i < str.length(); i++) {
            if (i * 3 + 3 > str.length()) {
                str2 += str.substring(i * 3, str.length());
                break;
            }
            str2 += str.substring(i * 3, i * 3 + 3) + ",";
        }
        if (str2.endsWith(",")) {
            str2 = str2.substring(0, str2.length() - 1);
        }
        //最后再将顺序反转过来
        String temp = new StringBuilder(str2).reverse().toString();
        if (isDecimal) {
            //去掉最后的","
            return temp.substring(0, temp.lastIndexOf(",")) + temp.substring(temp.lastIndexOf(",") + 1, temp.length());
        } else {
            return temp;
        }
    }

    /**
     * @Description 保持小数点后两位
     */
    public static String formatDoublePointTwo(double money) {
        DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(2);
        formater.setGroupingSize(0);
        formater.setRoundingMode(RoundingMode.FLOOR);
        return formater.format(money);
    }

    public static String urlEnodeUTF8(String str) {
        String result = str;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @return 字符串是否为空
     */
    public static Boolean isNullStr(String str) {
        try {
            Boolean result = false;

            if (str == null || str.length() <= 0 || str.equals(null)
                    || str.equals("null")) {
                result = true;
            }

            return result;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return 字符串是否不为空
     */
    public static Boolean isNotNullStr(String str) {
        try {
            Boolean result = true;

            if (str == null || str.length() <= 0 || str.equals(null)
                    || str.equals("null")) {
                result = false;
            }

            return result;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断一个字符串中是否包含指定字符串
     *
     * @param str         一个字符串
     * @param searchChars 指定字符串
     */
    public static boolean isHaveStr(String str, String searchChars) {
        try {
            if (isNotNullStr(str) && isNotNullStr(searchChars)) {
                return str.contains(searchChars);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断两个字符串是否相同
     *
     * @param str1 字符串1
     * @param str2 字符串2
     */
    public static boolean isSameStr(String str1, String str2) {
        try {
            if (isNotNullStr(str1) && isNotNullStr(str2)) {
                return str1.equals(str2);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * AES加密
     */
    public static String AES_Encrypt(String keyStr, String plainText) {
        try {
            Key key = generateKey(keyStr);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encodeToString(cipher.doFinal(plainText.getBytes()),
                    Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * AES解密
     */
    public static String AES_Decrypt(String keyStr, String encryptData) {
        try {
            Key key = generateKey(keyStr);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encrypted1 = Base64.decode(encryptData, Base64.DEFAULT);
            return new String(cipher.doFinal(encrypted1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static Key generateKey(String key) throws Exception {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            return keySpec;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 对外提供getMD5(String)方法
     *
     * @return 加密失败返回""
     */
    public static String getMD5(String val) throws NoSuchAlgorithmException {

        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    val.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }

        try {
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) {
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }

            return hex.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * get bin file's md5 string
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }


    /**
     * 生成32位编码
     *
     * @return string
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }

    /**
     * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串<br>
     * 实现步骤: <br>
     *
     * @param paraMap   要排序的Map对象
     * @param urlEncode 是否需要URLENCODE
     * @return
     */
    public static String formatUrlMap(Map<String, String> paraMap, boolean urlEncode) {
        String buff = "";
        Map<String, String> tmpMap = paraMap;
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).trim().compareTo(o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (StringUtil.isNotNullStr(item.getKey()) && StringUtil.isNotNullStr(item.getValue())) {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    buf.append(key + "=" + val);
                    buf.append("&");
                }

            }
            buff = buf.toString();
            if (!buff.isEmpty()) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            return null;
        }
        return buff;
    }


    public static String getShareTid(String id) {
        String value = "";
        try {
            value = getMD5(id + "s34ada").substring(2, 6);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static int checkObjFieldIsNotNull(Object obj){
        try {
            Field[] filedLength = obj.getClass().getDeclaredFields();
            int length = filedLength.length;
            int okLength = 0;
            for (Field f : filedLength) {
                f.setAccessible(true);//私有公共化
                if (f.get(obj) != null) {
                    okLength++;
                }
            }
            int permL=getnum(okLength,length);
            return permL;
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 计算两个数之间的百分比
     * @param y
     * @param z
     * @return
     */
    public static int getnum(int y,int z){
        String baifenbi = "";// 接受百分比的值
        double baiy = y * 1.0;
        double baiz = z * 1.0;
        double fen = baiy / baiz;
        // NumberFormat nf = NumberFormat.getPercentInstance(); 注释掉的也是一种方法
        // nf.setMinimumFractionDigits( 2 ); 保留到小数点后几位
        int myfen = (int) (fen*100);
        baifenbi = String.valueOf(myfen);
        System.out.println(baifenbi);

        return myfen;
    }
}
