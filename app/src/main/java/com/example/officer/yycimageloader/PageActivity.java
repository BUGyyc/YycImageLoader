package com.example.officer.yycimageloader;

import android.app.Activity;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by officer on 2015/12/23.
 */

public class PageActivity extends Activity implements View.OnTouchListener,View.OnClickListener,GestureDetector.OnGestureListener ,
        View.OnLongClickListener{

    public static final String TAG=PageActivity.class.getSimpleName();
    private Bitmap bitmap;
    ImageView ig;
    TextView txt;
    List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
    Map<String,Object> map;
    ImgTask imgTask;
    int page=0;//当前为第一页
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_page);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(imgTask!=null&&imgTask.getStatus()!= AsyncTask.Status.FINISHED){
            imgTask.cancel(true);
        }
    }

    private void initView(){
        ig=(ImageView)findViewById(R.id.lay_page_img);
        txt=(TextView)findViewById(R.id.lay_page_txt);
        new Thread(runnable).start();
        ig.setOnTouchListener(this);
        //ig.setOnClickListener(this);
        ig.setOnLongClickListener(this);
        initGesture();
    }

    private GestureDetector mGestureDetector;
    private int verticalMinDistance = 180;
    private int minVelocity         = 0;
    private void initGesture() {
        mGestureDetector = new GestureDetector((GestureDetector.OnGestureListener) this);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(bitmap!=null) {
            if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
               if(page==list.size()-1){
                   page=list.size()-1;
                   Toast.makeText(this,"这是最后一张",Toast.LENGTH_SHORT).show();
               }else{
                   page++;
                   imgTask = new ImgTask();
                   imgTask.execute(page + "");
               }
                //Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();
            } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
                if(page==0){
                    page=0;
                    Toast.makeText(this,"这是第一张",Toast.LENGTH_SHORT).show();
                }else {
                    page--;
                    imgTask = new ImgTask();
                    imgTask.execute(page + "");
                }
                //Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.lay_page_img:
                if (bitmap != null) {
                    ReadImageView.bitmap = bitmap;
                    startActivity(new Intent(this, ReadImageView.class));
                }
                break;
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
                            float arg3) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void onShowPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }




    Runnable runnable=new Runnable(){
        @Override
        public void run() {
            try {
                String url = "http://tu.duowan.com/scroll/120172.html";
                Connection conn = Jsoup.connect(url);
                // 修改http包中的header,伪装成浏览器进行抓取
                conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
                Document doc = conn.get();
                // 获取tbody元素下的所有tr元素
                Elements elements =doc.getElementsByClass("pic-box");
                //.select("img[src~=(?i)\\.(png|jpe?g)]");
                Log.v(TAG, "size    " + elements.size());
                for(Element element :elements){
                    String path=element.select("img").attr("src");
                    String tit=element.getElementsByTag("p").text();
                    Log.v(TAG, "    "+path+"   ");
                    map=new HashMap<String,Object>();
                    map.put("path",path);
                    map.put("tit",tit);
                    list.add(map);
                }
                Message message=new Message();
                message.what=0x111;
                handler.sendMessage(message);
            }catch(Exception e){
                    e.printStackTrace();
            }
        }
    };


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x111:
                    imgTask=new ImgTask();
                    imgTask.execute(page+"");
                    break;
            }
        }
    };

    class ImgTask extends AsyncTask<String,Void,Bitmap>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            try{
                if(isCancelled()){
                    return null;
                }
            URL url = new URL(list.get(Integer.parseInt(params[0])).get("path").toString());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
              }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            if(s!=null){
                ig.setImageBitmap(s);
                bitmap=s;
            }
        }
    }


}
