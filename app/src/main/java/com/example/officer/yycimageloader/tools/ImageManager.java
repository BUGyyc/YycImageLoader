package com.example.officer.yycimageloader.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by officer on 2015/12/21.
 */
public class ImageManager {
    /**
     *图片管理类
     */
    private static String SDPath= Environment.getExternalStorageDirectory().getPath();//内存卡的路径
    private static String PhonePath=null;//手机本地的路径
    private static final String FileName="/YycImage"; //文件夹名

    public ImageManager(Context context){
        PhonePath=context.getCacheDir().getPath();
    }

    private String getStorageDirectory(){
        //两种情况的路径
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                SDPath + FileName : PhonePath + FileName;
    }

    private void saveBitmap(String fileName,Bitmap bitmap) throws IOException {
        if(bitmap==null){
            return ;
        }
        String path=getStorageDirectory();
        File file=new File(path);
        if(!file.exists()){
            //不存在，就创建
            file.mkdir();
        }
        File mFile=new File(path+File.separator+fileName);
        mFile.createNewFile();
        FileOutputStream fileOutputStream=new FileOutputStream(mFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    /**
     * 文件是否存在
     * @param fileName
     * @return
     */
    public boolean isFileExists(String fileName){
        return new File(getStorageDirectory()+File.separator+fileName).exists();
    }

    private Bitmap getBitmap(String fileName){//获取到位图
        return BitmapFactory.decodeFile(getStorageDirectory() + File.separator + fileName);
    }

    /**
     * 删除文件
     * @param fileName
     */
    public void deleteFile(String fileName){
        File dirFile=new File(getStorageDirectory());//获取根路径
        if(!dirFile.exists()){
            return ;
        }
        if(dirFile.isDirectory()){
            String[]  file_all=dirFile.list();//获取文件路径下的所有文件
            for(int i=0;i<file_all.length;i++){
                new File(dirFile,file_all[i]).delete();//逐一删除
            }
        }
        dirFile.delete();
    }


    /**
     * 获取文件大小
     * @param fileName
     * @return
     */
    public long getFileSize(String fileName){
        return new File(getStorageDirectory()+File.separator+fileName).length();
    }


}
