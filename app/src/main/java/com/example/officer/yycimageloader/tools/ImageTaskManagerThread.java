package com.example.officer.yycimageloader.tools;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by officer on 2015/12/15.
 */
public class ImageTaskManagerThread implements Runnable{
    /**
     * 线程池
     */

    private ImageTaskManager imageTaskManager;
    private ExecutorService pool;//可重用固定线程数的线程池
    private final int SIZE=10;//线程数
    private final int SLEEP=1000;//轮询时间
    private boolean isStop=false;

    public ImageTaskManagerThread(){
        this.imageTaskManager=ImageTaskManager.getInstance();//获得实例
        pool= Executors.newFixedThreadPool(SIZE);
    }
    @Override
    public void run() {
        while(!isStop){
            ImageTask imageTask=imageTaskManager.getImageTask();
            if(imageTask!=null){
                pool.execute(imageTask);
            }else{
                try{
                    Thread.sleep(SLEEP);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        if(isStop){//关闭线程池
            pool.shutdown();
        }
    }

    public void setStop(boolean isStop){
        this.isStop=isStop;
    }
}
