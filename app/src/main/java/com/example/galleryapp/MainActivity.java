package com.example.galleryapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
private Button btn_album_view,btn_photo_view;
private ImageView imageView;
private final int MY_CAMERA_PERMISSION_CODE = 1;
private final int CAMERA_REQUEST=100;
private final int REQUEST_ID_READ_WRITE_PERMISSION=101;
    private final int REQUEST_ID_IMAGE_CAPTURE=1;
    private String mCurrentPhotoPath;
    private File output=null;
    private ListView lv;
    private PhotoAdapter customListAlarm;
    private ArrayList<String> listAlbum;
private DB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DB(getApplicationContext());
        lv=findViewById(R.id.listview);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);
        }
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Kiểm tra quyền đọc/ghi dữ liệu vào thiết bị lưu trữ ngoài.
            int readPermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (writePermission != PackageManager.PERMISSION_GRANTED ||
                    readPermission != PackageManager.PERMISSION_GRANTED) {

                // Nếu không có quyền, cần nhắc người dùng cho phép.
                this.requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_ID_READ_WRITE_PERMISSION
                );
            }
        }
        btn_album_view = findViewById(R.id.btn_albumVIew);
        btn_photo_view = findViewById(R.id.btn_anhView);
        listAlbum = db.getListAlbumTime();
        customListAlarm = new PhotoAdapter(getApplicationContext(),listAlbum,"DATE",MainActivity.this);
        lv.setAdapter(customListAlarm);
        btn_photo_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAlbum = db.getListAlbumTime();
                customListAlarm = new PhotoAdapter(getApplicationContext(),listAlbum,"DATE",MainActivity.this);
                lv.setAdapter(customListAlarm);
            }
        });
        btn_album_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAlbum = db.getListAlbum();
                customListAlarm = new PhotoAdapter(getApplicationContext(),listAlbum,"ALBUMID",MainActivity.this);
                lv.setAdapter(customListAlarm);
            }
        });

    }
    private void createImageFile(Bitmap bitmap,String album) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );
        // Save a file: path for use with ACTION_VIEW intents
        String path = image.getAbsolutePath();
        Photo photo = new Photo();
        photo.setId(album);
        photo.setPath(path);
        db.addPhoto(photo);
        customListAlarm.getimageAdapterGridView().addphoto(photo);
        customListAlarm.notifyDataSetChanged();
        customListAlarm.reload();
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        FileOutputStream out = new FileOutputStream(image);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        Log.i("IMAGE PATH",mCurrentPhotoPath);
    }
    private void captureImage() throws IOException {
            Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, REQUEST_ID_IMAGE_CAPTURE);
        // Create an implicit intent, for image capture.

        // Start camera and wait for the results.
    }
    private void askPermissionAndCaptureImage() throws IOException {

        // With Android Level >= 23, you have to ask the user
        // for permission to read/write data on the device.
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have read/write permission
            int readPermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int camerapermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);
            if (writePermission != PackageManager.PERMISSION_GRANTED ||
                    readPermission != PackageManager.PERMISSION_GRANTED ||
                    camerapermission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                        REQUEST_ID_READ_WRITE_PERMISSION
                );
                return;
            }
        }
        this.captureImage();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ImageView imageView;
                    Button btnsave,btncancel;
                    Spinner spinner;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_dialog, viewGroup, false);
                    imageView = dialogView.findViewById(R.id.dialog_image);
                    spinner = dialogView.findViewById(R.id.spinner);
                    btnsave = dialogView.findViewById(R.id.btn_save);
                    btncancel = dialogView.findViewById(R.id.btn_cancel);
                    imageView.setImageBitmap(bitmap);
                    ArrayList<String> items = new ArrayList<>();
                    items=db.getListAlbum();
                    System.out.println(items.toString());
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
                    spinner.setAdapter(adapter);
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                    btnsave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                createImageFile(bitmap,spinner.getSelectedItem().toString());
                                alertDialog.dismiss();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    btncancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                //this.imageView.setImageBitmap(bitmap);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Action canceled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Action Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
    // Khi yêu cầu hỏi người dùng được trả về (Chấp nhận hoặc không chấp nhận).
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_READ_WRITE_PERMISSION: {

                // Chú ý: Nếu yêu cầu bị hủy, mảng kết quả trả về là rỗng.
                // Người dùng đã cấp quyền (đọc/ghi).
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();
                }
                // Hủy bỏ hoặc bị từ chối.
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.action_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            // do something here
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_album, viewGroup, false);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            EditText editText = dialogView.findViewById(R.id.inp_album_name);
            Button btn_add = dialogView.findViewById(R.id.btn_add_albumm);
            Button btn_cancel = dialogView.findViewById(R.id.btn_cancel_album);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.addAlbum(editText.getText().toString());
                    listAlbum.add(editText.getText().toString());
                    customListAlarm.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            });
        }
        else if(id == R.id.opencamera){
            try {
                captureImage();
                customListAlarm.notifyDataSetChanged();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}