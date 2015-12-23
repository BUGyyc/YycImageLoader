package com.example.officer.yycimageloader.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by officer on 2015/12/15.
 */
public class ImageLoader {

    private class ImageHolder{
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }

    /**
     * 方便压缩
     */
    public class ImageSize{
        int width;
        int height;
    }

    /**
     * 提供缓存
     */
    public LruCache<String,Bitmap> mLruCache;

    /**
     * 线程池
     */
    private ExecutorService mThreadPool;

    /**
     * 任务队列
     * 队列放入方式
     */
    private LinkedList<Runnable> mTasks;

    private static ImageLoader instance;

    /**
     * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
     */
    private volatile Semaphore mSemaphore = new Semaphore(1);

    /**
     * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
     */
    private volatile Semaphore mPoolSemaphore;

    private Thread thread;
    private Handler poolHandler,handler;

    private Type mType = Type.FILO;
    /**
     * 线程数， 类型
     * @param count
     * @param type
     */
    private ImageLoader(int count,Type type){
        init(count,type);
    }

    private void init(int count,Type type){
        thread=new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    mSemaphore.acquire();//获得信号量
                }catch (Exception e){
                    e.printStackTrace();
                }
                Looper.prepare();

                poolHandler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        mThreadPool.execute(getTask());
                        try {
                            mPoolSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                // 释放一个信号量
                mSemaphore.release();
                Looper.loop();
            }
        };

        thread.start();

        int MaxMemory=(int)Runtime.getRuntime().maxMemory();
        int cacheSize=MaxMemory/8;
        mLruCache=new LruCache<String ,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return 0;
            }
        };

        mThreadPool= Executors.newFixedThreadPool(count);//新建线程池
        mPoolSemaphore=new Semaphore(count);
        mTasks=new LinkedList<Runnable>();
        if(type==null){
            type=Type.FILO;
        }
        this.mType=type;
    }

    public void loadImage(final String path,final ImageView imageView){
        imageView.setTag(path);
        if(handler==null){
            handler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    ImageHolder imageHolder=(ImageHolder)msg.obj;
                    ImageView ig=imageHolder.imageView;
                    Bitmap bit=imageHolder.bitmap;
                    String pat=imageHolder.path;
                    if(imageView.getTag().toString().equals(path)){
                        imageView.setImageBitmap(bit);
                    }
                }
            };
        }

        Bitmap bm=getBitmapFromLruCache(path);
        if(bm!=null){
            ImageHolder imageHolder=new ImageHolder();
            imageHolder.bitmap=bm;
            imageHolder.imageView=imageView;
            imageHolder.path=path;
            Message message=Message.obtain();
            message.obj=imageHolder;
            handler.sendMessage(message);
        }else{
            addTask(new Runnable(){
                @Override
                public void run() {
                    ImageSize imageSize=getImageViewSize(imageView);
                    int width=imageSize.width;
                    int height=imageSize.height;
                    Bitmap bm=decodeSampledBitmapFromResource(path,width,height);
                    addBitmap2LruCache(path,bm);
                    ImageHolder imageHolder=new ImageHolder();
                    imageHolder.bitmap=getBitmapFromLruCache(path);
                    imageHolder.imageView=imageView;
                    imageHolder.path=path;
                    Message message=Message.obtain();
                    message.obj=imageHolder;
                    handler.sendMessage(message);
                    mPoolSemaphore.release();
                }
            });
        }
    }

    /**
     * 添加任务
     */
    private synchronized void addTask(Runnable runnable){
        try{
            if(poolHandler==null){
                mSemaphore.acquire();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        mTasks.add(runnable);
        poolHandler.sendEmptyMessage(0x110);
    }

    private synchronized Runnable getTask(){
        if(mType==Type.FIFO){
            /**
             * 先进先出
             */
            return mTasks.removeFirst();
        }else{
            /**
             * 先进后出
             */
        return mTasks.removeLast();
        }
    }

    public static ImageLoader getInstance(){
        if(instance==null){
            synchronized (ImageLoader.class){
                if(instance==null){
                    instance=new ImageLoader(1,Type.FILO);//默认选择其中一种先进后出的方式
                }
            }
        }
        return instance;
    }

    private ImageSize getImageViewSize(ImageView imageView){
        ImageSize imageSize=new ImageSize();
        final DisplayMetrics displayMetrics=imageView.getContext()
                .getResources().getDisplayMetrics();
        final ViewGroup.LayoutParams layoutParams=imageView.getLayoutParams();
        int width=layoutParams.width;

        if(width== ViewGroup.LayoutParams.WRAP_CONTENT){
            width=0;
        }else{
            width=imageView.getWidth();
        }
        if(width<=0){
            width=layoutParams.width;
        }
        if(width<=0){
            width=getImageViewFieldValue(imageView,"mMaxWidth");
        }

        if (width <= 0)
            width = displayMetrics.widthPixels;
        int height = layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getHeight(); // Get actual image height
        if (height <= 0)
            height = layoutParams.height; // Get layout height parameter
        if (height <= 0)
            height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
        // maxHeight
        // parameter
        if (height <= 0)
            height = displayMetrics.heightPixels;
        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    /**
     * 从缓存中获取图片
     * @param key
     * @return
     */
    private Bitmap getBitmapFromLruCache(String key){
        return mLruCache.get(key);
    }

    /**
     * 添加图片至缓存
     * 图片重复或者为空则无需添加
     * @param key
     * @param bitmap
     */
    private void  addBitmap2LruCache(String key,Bitmap bitmap){
        if(getBitmapFromLruCache(key)==null){
            if(bitmap!=null){
                mLruCache.put(key,bitmap);
            }
        }
    }

    /**
     * 定义两种类型
     * 先进先出
     * 先进后出
     */
    public enum Type{
        FIFO,FILO
    }

    /**
     * 获得压缩比例
     * @param options
     * @param width
     * @param height
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options,int width,int height){
        int wid=options.outWidth;
        int hei=options.outHeight;
        int inSampleSize=1;
        if(wid>width&&hei>height){
            // 计算出实际宽度和目标宽度的比率
            int widthRatio = Math.round((float) wid / (float) width);
            int heightRatio = Math.round((float) hei / (float) height);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    /**
     * 压缩图片
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampledBitmapFromResource(String path,int width,int height){
        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path,options);
        options.inSampleSize=calculateInSampleSize(options,width,height);
        options.inJustDecodeBounds=false;
        Bitmap bitmap=BitmapFactory.decodeFile(path,options);
        return bitmap;
    }

    private static int getImageViewFieldValue(Object object,String name){
        int value=0;
        try{
            Field field=ImageView.class.getDeclaredField(name);
            field.setAccessible(true);
            int fieldValue=(Integer)field.get(object);
            if(fieldValue>0&&fieldValue<Integer.MAX_VALUE){
                value=fieldValue;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return value;
        }

    }


}
