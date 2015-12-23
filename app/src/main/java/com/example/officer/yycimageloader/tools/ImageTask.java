package com.example.officer.yycimageloader.tools;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.example.officer.yycimageloader.MainActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by officer on 2015/12/15.
 */
public class ImageTask implements Runnable{
    /**
     * 图片任务
     */
    //缓存存放图片
    private LruCache<String, Bitmap> mLruCache;

    public static final String TAG=ImageTask.class.getSimpleName();
    String name;
    Context mContext;
    public ImageTask(String name,Context context){
        this.name=name;
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize);
        mContext=context;
    }
    @Override
    public void run() {
//        // 读取uri所在的图片
//        try {
//            Uri uri=Uri.parse(name);
//            Bitmap bitmap = MediaStore.Images.Media.
//                    getBitmap(mContext.getContentResolver(), uri);
//            mLruCache.put("bit",bitmap);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        try{
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.v(TAG,name+"  完成加载");
    }



    public String getName(){
        return this.name;
    }


}
