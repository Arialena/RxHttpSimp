package com.wy.arialena.modulecore.update_app.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogUtil {
    private LogUtil(){
    }

    private static String           TAG = "LogToFile";
    public static final boolean   LOG_RES = true;
    /**
     * og日志存放路径
     * 日期格式;
     * 因为log日志是使用日期命名的，使用静态成员变量主要是为了在整个程序运行期间只存在一个.log文件中;
     */
    public static String            logPath = null;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
    private static Date date = new Date();

    /**
     * 初始化，须在使用之前设置，最好在Application创建时调用
     * 获得文件储存路径,在后面加"/Logs"建立子文件夹
     */
    public static void init(Context context) {
        logPath = getFilePath(context) + "/jytc/Logs/";
    }

    /**
     * 获得文件存储路径
     *
     * @return
     */
    public static String getFilePath(Context context) {

        if (Environment.MEDIA_MOUNTED.equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            return context.getExternalFilesDir(null).getAbsolutePath();
        } else {
            return context.getCacheDir().getAbsolutePath();
        }
    }

    private static final char VERBOSE = 'v';

    private static final char DEBUG = 'd';

    private static final char INFO = 'i';

    private static final char WARN = 'w';

    private static final char ERROR = 'e';

    public static void v(String tag, String msg) {
        if (LOG_RES){
            Log.v(tag, msg);
            writeToFile(VERBOSE, tag, msg);
        }
    }

    public static void d(String tag, String msg) {
       if (LOG_RES){
           Log.d(tag, msg);
           writeToFile(DEBUG, tag, msg);
       }
    }

    public static void i(String tag, String msg) {
       if (LOG_RES){
           Log.i(tag, msg);
           writeToFile(INFO, tag, msg);
       }
    }

    public static void w(String tag, String msg) {
       if (LOG_RES){
           Log.w(tag, msg);
           writeToFile(WARN, tag, msg);
       }
    }

    public static void e(String tag, String msg) {
       if (LOG_RES){
           Log.e(tag, msg);
           writeToFile(ERROR, tag, msg);
       }
    }

    /**
     * 将log信息写入文件中
     *
     * @param type
     * @param tag
     * @param msg
     */
    private static void writeToFile(char type, String tag, String msg) {

        if (null == logPath) {
            LogUtil.e(TAG, "logPath == null ，未初始化LogToFile");
            return;
        }

        //long now = android.os.SystemClock.uptimeMillis();
        long time = System.currentTimeMillis();
        String fileName = logPath + dateFormat.format(new Date(time)) + ".log";
        String log = dateFormat.format(date) + " " + type + " " + tag + " " + msg + "\n";

        //如果父路径不存在
        File file = new File(logPath);
        if (!file.exists()) {
            //创建父路径
            file.mkdirs();
        }

        /**
         *FileOutputStream会自动调用底层的close()方法，不用关闭
         */
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {

//            这里的第二个参数代表追加还是覆盖，true为追加，flase为覆盖
            fos = new FileOutputStream(fileName, true);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(log);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}