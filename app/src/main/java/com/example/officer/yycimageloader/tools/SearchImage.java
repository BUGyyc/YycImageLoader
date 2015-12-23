package com.example.officer.yycimageloader.tools;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by officer on 2015/12/15.
 */
public class SearchImage {

    /**
     * 搜索本地所有图片
     */
    public static final String TAG=SearchImage.class.getSimpleName();
    Map<String,Object> map;
    private Context mContext;
    List<String> findPath=new ArrayList<String>();
    private int mPicNum;//图片数目
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;

    private List<String> mImgs;

    public SearchImage(Context context){
        this.mContext=context;
        new Thread(runnable).start();
    }





    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            Uri imageUri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver=mContext.getContentResolver();
            //获取ContentResolver,用来检索图片路径
            Cursor mCursor=mContentResolver.query(imageUri, null,
                    MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[] { "image/jpeg", "image/png" },
                    MediaStore.Images.Media.DATE_MODIFIED);
//            检索图片  jpeg,png,jpg
            while(mCursor.moveToNext()){//继续往下一个节点查找
                String imagePath=mCursor.getString(
                        mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                File TopPath=new File(imagePath).getParentFile();//往上一层的父路径
                String absolutePath=TopPath.getAbsolutePath();//获取绝对路径
                if(findPath.contains(absolutePath)){//防止多次扫描同一个路径
                    continue;
                }else{
                    findPath.add(absolutePath);
                }
                int picNum=TopPath.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        if (filename.endsWith(".jpg")) {
                            return true;
                        }
                        return false;
                    }
                }).length;
                if(mPicNum<picNum){//获取最终的最大值
                    mPicNum=picNum;
                    mImgDir = TopPath;
                }
            }
            mCursor.close();
            //扫描完成，辅助的HashSet也就可以释放内存了
            findPath = null ;
            // 通知Handler扫描图片完成
            mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if (filename.endsWith(".jpg"))
                        return true;
                    return false;
                }
            }));
            Log.i(TAG,"     "+mImgs+"   "+mImgDir);
            ImageUtil.setPicName(mImgs);
            ImageUtil.setDirName(mImgDir);
        }
    };
}
