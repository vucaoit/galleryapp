package com.example.galleryapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class PhotoAdapter extends BaseAdapter {
private ArrayList<String> listData;
    private LayoutInflater layoutInflater;
    private Context context;
    private String condition;
    private ArrayList<Photo> photoArrayList;
    private DB db = new DB(context);
    private ImageAdapterGridView imageAdapterGridView;
    private Activity parentActivity;
    public PhotoAdapter(Context context,ArrayList<String> listData,String condition,Activity activity) {
        this.listData = listData;
        this.parentActivity=activity;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.condition=condition;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.recycle_photo_view, null);
        }
        TextView tv_alarm = (TextView) convertView.findViewById(R.id.txt_title1);
        GridView androidGridView = convertView.findViewById(R.id.gridview_android_example);
        tv_alarm.setText(listData.get(position));
        DB db12 = new DB(context);
        photoArrayList=db12.getAllPhoto(condition,listData.get(position));
        imageAdapterGridView = new ImageAdapterGridView(context,photoArrayList,parentActivity);
        androidGridView.setAdapter(imageAdapterGridView);
        return convertView;
    }
    public ImageAdapterGridView getimageAdapterGridView(){
        return imageAdapterGridView;
    }
    public class ImageAdapterGridView extends BaseAdapter {
        private Context mContext;
        private ArrayList<Photo> listPhoto;
        private Activity mainactivity;
        public ImageAdapterGridView(Context c,ArrayList<Photo> photos,Activity mainactivity) {
            mContext = c;
            this.mainactivity=mainactivity;
            this.listPhoto=photos;
        }

        public int getCount() {
            return listPhoto.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView mImageView;
            if (convertView == null) {
                mImageView = new ImageView(mContext);
                mImageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mImageView.setPadding(16, 16, 16, 16);
            } else {
                mImageView = (ImageView) convertView;
            }
            File imgFile = new  File(listPhoto.get(position).getPath());

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                mImageView.setImageBitmap(myBitmap);
                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent= new Intent(context,MainActivity2.class);
                        intent.putExtra("PATH",listPhoto.get(position).getPath());
                        mContext.startActivity(intent);
                    }
                });
            }
            return mImageView;
        }
        public void addphoto(Photo photo){
            listPhoto.add(photo);
        }
    }
    public void reload(){
        imageAdapterGridView.notifyDataSetChanged();
    }
}

