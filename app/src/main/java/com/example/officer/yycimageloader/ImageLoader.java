//package com.example.officer.yycimageloader;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Handler;
//
//import java.io.InputStream;
//import java.lang.ref.SoftReference;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * Created by officer on 2015/12/24.
// */
//public class ImageLoader {
//    public Map<String,SoftReference<Bitmap>> bitmapCache=
//            new HashMap<String,SoftReference<Bitmap>>();
//    private ExecutorService executorService= Executors.newFixedThreadPool(3);//
//    private Handler handler=new Handler();
//    public Bitmap loadBitmap(final String url,final CallImageBack callImageBack){
//        if(bitmapCache.containsKey(url)){
//            /**
//             * Java中的SoftReference
//             即对象的软引用。如果一个对象具有软引用，内存空间足够，垃圾回收器就不会回收它；
//             如果内存空间不足了，就会回收这些对象的内存。只要垃圾回收器没有回收它，
//             该对象就可以被程序使用。软引用可用来实现内存敏感的高速缓存。使用软引用能防止内存泄露，
//             增强程序的健壮性。SoftReference的特点是它的一个实例保存对一个Java对象的软引用，
//             该软引用的存在不妨碍垃圾收集线程对该Java对象的回收。也就是说，
//             一旦SoftReference保存了对一个Java对象的软引用后，在垃圾线程对这个Java对象回收前，
//             SoftReference类所提供的get()方法返回Java对象的强引用。另外，
//             一旦垃圾线程回收该Java对象之后，get()方法将返回null
//             */
//            SoftReference<Bitmap> softReference=bitmapCache.get(url);
//            if(softReference.get()!=null){
//                return softReference.get();
//            }
//        }
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    final Bitmap bitmap=getImageFromUrl(url);
//                    bitmapCache.put(url,new SoftReference<Bitmap>(bitmap));
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            callImageBack.getBitmap(bitmap);
//                        }
//                    });
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });
//        return null;
//    }
//
//    public Bitmap getBitmap(final String url){
//        if(bitmapCache.containsKey(url)){
//            /**
//             * Java中的SoftReference
//             即对象的软引用。如果一个对象具有软引用，内存空间足够，垃圾回收器就不会回收它；
//             如果内存空间不足了，就会回收这些对象的内存。只要垃圾回收器没有回收它，
//             该对象就可以被程序使用。软引用可用来实现内存敏感的高速缓存。使用软引用能防止内存泄露，
//             增强程序的健壮性。SoftReference的特点是它的一个实例保存对一个Java对象的软引用，
//             该软引用的存在不妨碍垃圾收集线程对该Java对象的回收。也就是说，
//             一旦SoftReference保存了对一个Java对象的软引用后，在垃圾线程对这个Java对象回收前，
//             SoftReference类所提供的get()方法返回Java对象的强引用。另外，
//             一旦垃圾线程回收该Java对象之后，get()方法将返回null
//             */
//            SoftReference<Bitmap> softReference=bitmapCache.get(url);
//            if(softReference.get()!=null){
//                return softReference.get();
//            }
//        }
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    final Bitmap bitmap=getImageFromUrl(url);
//                    bitmapCache.put(url,new SoftReference<Bitmap>(bitmap));
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    });
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });
//        return null;
//    }
//    protected Bitmap getImageFromUrl(String imgurl){
//        Bitmap bit=null;
//        try{
//            URL url = new URL(imgurl);
//            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//            conn.setConnectTimeout(5000);
//            conn.setRequestMethod("GET");
//            if(conn.getResponseCode() == 200) {
//                InputStream inputStream = conn.getInputStream();
//                bit= BitmapFactory.decodeStream(inputStream);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            return bit;
//        }
//    }
//
//
//    //回调接口
//    public interface CallImageBack{
//        void getBitmap(Bitmap  bitmap);
//    }
//}
