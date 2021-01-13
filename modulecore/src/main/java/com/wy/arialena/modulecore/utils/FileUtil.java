package com.wy.arialena.modulecore.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

/**
 * 文件处理相关工具类
 * @author wuyan
 */
public class FileUtil {
    /**
     * 创建新文件加
     * @param context
     * @return
     * @throws IOException
     */
    public static File createImageFile(Context context, String path) throws IOException {
        //创建保存的路径
        File storageDir = getOwnCacheDirectory(context);

        File image = new File(storageDir.getAbsolutePath(), path);
        if (!image.exists()) {
            try {
                //在指定的文件夹中创建文件
                image.createNewFile();
            } catch (Exception e) {
            }
        }
        return image;
    }

    /**
     * 根据目录创建文件夹
     * @param context
     * @return
     */
    public static File getOwnCacheDirectory(Context context) {
        File appCacheDir = null;
        //判断sd卡正常挂载并且拥有权限的时候创建文件
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        }
        if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    /**
     * 检查是否有权限
     * @param context
     * @return
     */
    public static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return perm == 0;
    }

    /**
     * 创建时间:2019/1/26
     * 注释描述:获取缓存目录  临时缓存
     * @fileName 获取外部存储目录下缓存的 fileName的文件夹路径
     */
    public static boolean getDiskCacheDir(Context context, String fileName) {
        String cachePath = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {//此目录下的是外部存储下的私有的fileName目录
                cachePath = context.getExternalCacheDir().getPath() + "/" + fileName;  //SDCard/Android/data/你的应用包名/cache/fileName
            } else {
                cachePath = context.getCacheDir().getPath()+ "/" + fileName;
            }
            File file = new File(cachePath);
            if (!file.exists()){
                return false;
            }
            return true; //SDCard/Android/data/你的应用包名/cache/fileName/
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 创建时间:2019/1/26
     * 注释描述:获取缓存目录
     * @fileName 获取外部存储目录下缓存的 fileName的文件夹路径
     */
    public static String getImgCropressPath(Context context) {
        String cachePath = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {//此目录下的是外部存储下的私有的fileName目录
                cachePath = context.getExternalCacheDir().getPath() + "/";  //SDCard/Android/data/你的应用包名/cache/fileName
            } else {
                cachePath = context.getCacheDir().getPath()+ "/";
            }
            return cachePath; //SDCard/Android/data/你的应用包名/cache/fileName/
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 创建临时缓存文件
     * @param context
     * @param fileName
     */
    public static void createCacheFile(Context context,String fileName) {
        String cachePath = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {//此目录下的是外部存储下的私有的fileName目录
                cachePath = context.getExternalCacheDir().getPath() + "/" + fileName;
            } else {
                cachePath = context.getCacheDir().getPath()+ "/" + fileName;
            }
            File file = new File(cachePath);
            if (!file.exists()){
                file.mkdirs();
            }
        }catch (Exception e){

        }
    }

    public static String createFilePath(Context context,String fileName) {
        String cachePath = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {//此目录下的是外部存储下的私有的fileName目录
                cachePath = context.getExternalCacheDir().getPath() + "/" + fileName;  //SDCard/Android/data/你的应用包名/cache/fileName
            } else {
                cachePath = context.getCacheDir().getPath()+ "/" + fileName;
            }
            return cachePath;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 从Uri中获取图片路径
     *
     * @param context
     * @param uri     图片Uri
     * @return
     */
    public static String getImagePathFromUri(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Uri stringToUri(String path){
        try {
            Uri uri = Uri.parse(path);
            return uri;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 把字节数组保存为一个文件
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 重命名文件
     *
     * @param oldPath 原来的文件地址
     * @param newPath 新的文件地址
     */
    public static File renameFile(String oldPath, String newPath) {
        File oleFile = new File(oldPath);
        File newFile = new File(newPath);
//执行重命名
         boolean b = oleFile.renameTo(newFile);
        if (b){
            return oleFile;
        }
        return oleFile;
    }

    /**
     * 获取文件格式名
     */
    public static String getFormatName(String fileName) {
        //去掉首尾的空格
        fileName = fileName.trim();
        String s[] = fileName.split("\\.");
        if (s.length >= 2) {
            return s[s.length - 1];
        }
        return "";
    }

    /**
     * 向文件中添加内容
     *
     * @param strcontent 内容
     * @param filePath   地址
     * @param fileName   文件名
     */
    public static void writeToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写

        File subfile = new File(strFilePath);


        RandomAccessFile raf = null;
        try {
            /**   构造函数 第二个是读写方式    */
            raf = new RandomAccessFile(subfile, "rw");
            /**  将记录指针移动到该文件的最后  */
            raf.seek(subfile.length());
            /** 向文件末尾追加内容  */
            raf.write(strcontent.getBytes());

            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 读取文件内容
     *
     * @param filePath 地址
     * @param filename 名称
     * @return 返回内容
     */
    public static String getFileContentString(String filePath, String filename) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(filePath + filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
