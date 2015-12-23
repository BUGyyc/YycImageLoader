package com.example.officer.yycimageloader.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by officer on 2015/12/15.
 */
public class ImageUtil {
    public static List<String> picName=new ArrayList<String>();//文件名
    public static File dirName;//路径名
    public ImageUtil(List<String> list,File dir){
        this.picName=list;
        this.dirName=dir;
    }

    public static void setPicName(List<String> list){
        picName=list;
    }

    public static void setDirName(File dir){
        dirName=dir;
    }

    public static List<String> getPicName(){
        return picName;
    }

    public static File getDirName(){
        return dirName;
    }
}
