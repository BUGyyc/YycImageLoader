package com.example.officer.yycimageloader;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.officer.yycimageloader.tools.ImageTask;
import com.example.officer.yycimageloader.tools.ImageTaskManager;
import com.example.officer.yycimageloader.tools.ImageTaskManagerThread;
import com.example.officer.yycimageloader.tools.ImageUtil;
import com.example.officer.yycimageloader.tools.SearchImage;

import java.io.File;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn=(Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  test_pool();
                synchronized (this) {
                   // test_find();
                    test_pool();
                }
            }
        });
    }

    private void test_find(){
        SearchImage s=new SearchImage(this);
    }


    /**
     * 线程池
     */
    private void  test_pool(){
        ImageTaskManager imageTaskManager=ImageTaskManager.getInstance();
        ImageTaskManagerThread imageTaskManagerThread=new ImageTaskManagerThread();
        new Thread(imageTaskManagerThread).start();

//        File f= ImageUtil.getDirName();
//        List<String> list=ImageUtil.getPicName();

        String []items={"图1"
                ,"图2",
                "图3",
                "图4",
                "图5",
                "图6"};

        for(int i=0;i<items.length;i++){
            imageTaskManager.addImageTask(new ImageTask(items[i],this));//添加任务进线程池
                try{
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
