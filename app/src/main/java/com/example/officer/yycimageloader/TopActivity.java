package com.example.officer.yycimageloader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.GridView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by officer on 2015/12/24.
 */
public class TopActivity extends Activity{
    public static final String TAG=TopActivity.class.getSimpleName();
    private GridView gridView;
    Map<String,Object> map;
    List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
    TopPageAdapter top;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_top);
        initView();
        new Thread(runnable).start();
    }

    private void initView(){
        gridView=(GridView)findViewById(R.id.lay_top_grid);
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try {
                String url = "http://tu.duowan.com/tag/12605.html";
                Connection conn = Jsoup.connect(url);
                // 修改http包中的header,伪装成浏览器进行抓取
                conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
                Document doc = conn.get();
                // 获取tbody元素下的所有tr元素
                Elements elements =doc.getElementsByClass("i-list");
                Elements el=elements.select("a[target=_blank]");
                Log.v(TAG, "size    " + el.size());
                for(Element element :el){
                    String path=element.select("img").attr("src");
                    String tit=element.getElementsByTag("p").text();
                    Log.v(TAG, "    " + path + "   ");
                    map=new HashMap<String, Object>();
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
            switch(msg.what){
                case 0x111:
                    top=new TopPageAdapter(TopActivity.this,list);
                    gridView.setAdapter(top);
                    break;
            }
        }
    };
}
