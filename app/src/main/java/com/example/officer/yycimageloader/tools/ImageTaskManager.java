package com.example.officer.yycimageloader.tools;

import android.util.Log;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by officer on 2015/12/15.
 */
public class ImageTaskManager {
    /**
     * 图片任务管理
     */
    public static final String TAG=ImageTaskManager.class.getSimpleName();
    //请求线程队列
    private LinkedList<ImageTask> imageTasks;
    //任务不能重复
    private Set<String> taskIdSet;

    private static ImageTaskManager imageTaskManager;

    public static synchronized ImageTaskManager getInstance(){
        if (null == imageTaskManager) {
            imageTaskManager = new ImageTaskManager();
        }
        return imageTaskManager;
    }

    private ImageTaskManager() {
        imageTasks = new LinkedList<ImageTask>();
        taskIdSet = new HashSet<String>();
    }

    public void addImageTask(ImageTask downloadTask){
        synchronized (imageTasks) {
            if (!isTaskRepeat(downloadTask.getName())) {
                imageTasks.addLast(downloadTask);
            }
        }
    }

    public boolean isTaskRepeat(String fileId) {
        synchronized (taskIdSet) {
            if (taskIdSet.contains(fileId)) {
                return true;
            } else {
               Log.v(TAG,"任务管理  添加任务" + fileId);
                taskIdSet.add(fileId);
                return false;
            }
        }
    }

    public ImageTask getImageTask() {
        synchronized (imageTasks) {//强制同步
            if (imageTasks.size() > 0) {
                Log.v(TAG,"任务管理   " + "取出任务");
                ImageTask imageTask = imageTasks.removeFirst();//先进先出，取出顶部的任务
                return imageTask;
            }
        }
        return null;
    }
}
