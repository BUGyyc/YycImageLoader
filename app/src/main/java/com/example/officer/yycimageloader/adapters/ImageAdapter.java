package com.example.officer.yycimageloader.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.officer.yycimageloader.R;
import com.example.officer.yycimageloader.tools.ImageLoader;

import java.util.List;
import java.util.Map;

/**
 * Created by officer on 2015/12/22.
 */
public class ImageAdapter extends BaseAdapter {

    private static class ViewHolder{
        ImageView ig;
    }

    private Context context;
    private List<String> list;
    private String dirPath;//路径
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;
    public ImageAdapter(Context context,List<String> data,String path){
        this.context=context;
        this.list=data;
        this.dirPath=path;
        layoutInflater=LayoutInflater.from(context);

        imageLoader=ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=layoutInflater.inflate(R.layout.item,parent,false);
            viewHolder.ig=(ImageView)convertView.findViewById(R.id.item_ig);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.ig.setImageResource(R.mipmap.ic_launcher);
      //  imageLoader.loadImage(dirPath+"/"+list.get(position));
        return convertView;
    }
}
