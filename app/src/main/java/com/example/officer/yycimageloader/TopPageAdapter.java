package com.example.officer.yycimageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.officer.yycimageloader.loaders.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by officer on 2015/12/24.
 */
public class TopPageAdapter extends BaseAdapter {

    static class ViewHolder{
        ImageView img;
        TextView txt;
    }
    private ImageLoader mImageLoader;
    private Context context;
    private List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
    private LayoutInflater layoutInflater;
    public TopPageAdapter(Context context,List<Map<String,Object>> list){
        layoutInflater=LayoutInflater.from(context);
        this.context=context;
        this.list=list;
        mImageLoader = ImageLoader.getInstance(3 , ImageLoader.Type.LIFO);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=layoutInflater.inflate(R.layout.item,parent,false);
            viewHolder.img=(ImageView)convertView.findViewById(R.id.item_ig);
            viewHolder.txt=(TextView)convertView.findViewById(R.id.item_txt);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.img.setImageResource(R.mipmap.ic_launcher);
        //使用Imageloader去加载图片
        mImageLoader.loadImage(list.get(position).get("path").toString(),
                viewHolder.img);
//        Bitmap bit=imageLoader.getBitmap(list.get(position).get("path").toString());
//        if(bit!=null) {
//            viewHolder.img.setImageBitmap(bit);
//            viewHolder.txt.setText("123");
//        }
        return convertView;
    }


}
