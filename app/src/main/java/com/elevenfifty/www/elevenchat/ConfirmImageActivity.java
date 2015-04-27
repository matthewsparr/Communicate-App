package com.elevenfifty.www.elevenchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.elevenfifty.www.elevenchat.Models.ChatPicture;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfirmImageActivity extends Activity {
    private String timestamp;
    private String pictureCode = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_image);

        final ImageView confirmImage = (ImageView) findViewById(R.id.confirmImage);
        confirmImage.setVisibility(View.VISIBLE);

//        ImageView image = (ImageView) findViewById(R.id.image);
//        image.setVisibility(View.INVISIBLE);

        Button button = (Button) findViewById(R.id.goToCamera);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmImage.setVisibility(View.INVISIBLE);
                finish();
            }
        });

        Intent intent = getIntent();
        timestamp = intent.getStringExtra("timestamp");
        LoadImageTask task = new LoadImageTask(confirmImage);
        task.execute("IMG_"+ timestamp + ".jpg");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingImage();
            }
        }, 3000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                sendImage(confirmImage);
            }
        }, 6000);



    }



    public void loadingImage() {

        final Dialog loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.custom_dialog);
        int dialogDivider = loadingDialog.getContext().getResources().getIdentifier("titleDivider", "id", "android");
        View askingtitleDivider = loadingDialog.getWindow().getDecorView().findViewById(dialogDivider);
        askingtitleDivider.setBackgroundColor(Color.TRANSPARENT);
        loadingDialog.setTitle("Communicating");
        loadingDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.hide();
                loadingDialog.dismiss();

            }
        }, 2000);
        loadImage();

    }
    public void loadImage() {
        final Firebase chatPictureRef = new Firebase("https://communicate.firebaseio.com/").child("Photo");
//        Query query = chatPictureRef.orderByKey().limitToLast(2);
//        chatPictureRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                pictureCode = dataSnapshot.getValue().toString();
//
//                ImageView image = (ImageView) findViewById(R.id.image);
//
//                byte[] bytes = Base64.decode(pictureCode, Base64.DEFAULT);
//                Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                image.setImageBitmap(picture);
//            }
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });


//        Query query = chatPictureRef.orderByKey().limitToFirst(1);
        chatPictureRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                pictureCode = dataSnapshot.getValue().toString();

                ImageView image = (ImageView) findViewById(R.id.image);

                byte[] bytes = Base64.decode(pictureCode, Base64.DEFAULT);
                Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(picture);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void sendImage(View view) {

        String imageString = null;
        String imageTime = timestamp;
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        File imageFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ imageTime + ".jpg");
        try {
            InputStream inputStream = new FileInputStream(imageFile);
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = output.toByteArray();
            imageString = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Firebase chatPictureRefs = new Firebase("https://communicate.firebaseio.com/");
//        final ImageStringTask task = new ImageStringTask();
        final Context context = this;
//        task.setTaskCompletionListener(new ImageStringTask.OnTaskCompletion() {
//            @Override
//            public void setMyTaskComplete() {
//                String imageString = task.imageString;

                    Map<String, Object> photo = new HashMap<String, Object>();
                    photo.put("photo", imageString);
                    Log.d("isworking", "wokring");
                    chatPictureRefs.child("Photo").child("Photo").setValue(imageString);
                    chatPictureRefs.child("photos").push().setValue(imageString);
//                    Firebase save = chatPictureRef.child("Photos");
//                    save.push().setValue(imageString);

//                }

//        });
//        task.execute(timestamp);



    }
}

//class ImageStringTask extends AsyncTask<String, Void, String> {
//    private OnTaskCompletion onTaskCompletion;
//    public String imageString;
//
//    public interface OnTaskCompletion {
//        public void setMyTaskComplete();
//    }
//
//    public void setTaskCompletionListener(OnTaskCompletion onTaskCompletion) {
//        this.onTaskCompletion = onTaskCompletion;
//    }
//
//    public ImageStringTask() { }
//
//    @Override
//    protected String doInBackground(String... params) {
//        String imageTime = params[0];
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "MyCameraApp");
//        File imageFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ imageTime + ".jpg");
//        try {
//            InputStream inputStream = new FileInputStream(imageFile);
//            byte[] bytes;
//            byte[] buffer = new byte[8192];
//            int bytesRead;
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//            try {
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    output.write(buffer, 0, bytesRead);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            bytes = output.toByteArray();
//            imageString = Base64.encodeToString(bytes, Base64.DEFAULT);
//            return imageString;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return "empty";
//        }
//    }
//}