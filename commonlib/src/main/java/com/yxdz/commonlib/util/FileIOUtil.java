package com.yxdz.commonlib.util;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @PackageName: com.yxdz.commonlib.util
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/2/19 17:03
 */
public class FileIOUtil {

    public static String TAG="FileIOUtil";

    public static void save(String fileName, Bitmap bitmap){
        String dir= getFaceDir();
        try {
            File file = new File(dir + File.separator + fileName + ".jpg");
            LogUtils.d(TAG,"face path:"+dir + File.separator + fileName + ".jpg"+"---"+bitmap);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG,e.getMessage());
        }

    }


    public static void saveFile(byte[] data, String fileName) {
        // 创建String对象保存文件名路径
        try {
            // 创建指定路径的文件
            File file = new File(getFaceDir(), fileName);
            // 如果文件不存在
            if (file.exists()) {
                // 创建新的空文件
                file.delete();
            }
            file.createNewFile();
            LogUtils.d(TAG,"face path:"+file.getAbsolutePath());
            // 获取文件的输出流对象
            FileOutputStream outStream = new FileOutputStream(file);
            // 获取字符串对象的byte数组并写入文件流
            outStream.write(data);
            // 最后关闭文件输出流
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean delete(String fileName){
        String dir=getFaceDir();
        String fileStr=dir+File.separator+fileName;
        File file=new File(fileStr);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }else{
            return false;
        }
    }

    public static String getFaceDir(){
        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"face");
        LogUtils.d(TAG,Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"face");
        if (file.exists()){
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "face";
        }else{
            file.mkdirs();
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"face";
        }
    }

    /**
     * 删除文件夹以及目录下的文件
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean  deleteFaceDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                String absolutePath = files[i].getName();
                flag = delete(absolutePath);
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteFaceDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }


}
