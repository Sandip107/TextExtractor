package com.example.textscanner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    EditText editText;
    FloatingActionButton addfab,addcam,addgal;
    private static Boolean isAllFabsVisible;
    ImageView imageView;
    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=400;
    private static final int IMAGE_PICK_GALLERY_CODE=1000;
    private static final int IMAGE_PICK_CAMERA_CODE=1001;
    String cameraPermission[];
    String storagePermission[];
    Uri image_uri;
    String str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Animation an1=AnimationUtils.loadAnimation(this,R.anim.from_button_anim);
        Animation an2=AnimationUtils.loadAnimation(this,R.anim.rotate_colse_anim);
        Animation an3=AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim);
        Animation an4=AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim);
        setContentView(R.layout.activity_main);
        ActionBar actionBar=getSupportActionBar();
        addfab=findViewById(R.id.add_fab);
        addcam=findViewById(R.id.add__cam);
        addgal=findViewById(R.id.add_gall);
        editText=findViewById(R.id.EditText);
        imageView=findViewById(R.id.imageView);
        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        isAllFabsVisible = false;
        addfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllFabsVisible){
                    addcam.show();
                    addcam.startAnimation(an1);
                    addgal.show();
                    addgal.startAnimation(an1);
                    addfab.startAnimation(an3);
                    isAllFabsVisible =true;
                }
                else {
                    addcam.hide();
                    addcam.startAnimation(an4);
                    addgal.hide();
                    addgal.startAnimation(an4);
                    addfab.startAnimation(an2);
                    isAllFabsVisible=false;
                }
            }
        });
        addcam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkCameraPermission())
                {
                    requestCameraPermission();
                }
                else {
                    pickCamera();
                }
            }
        });
        addgal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkStoragePermission())
                {
                    requestStoragePermission();
                }
                else {
                    pickGallery();
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    private void pickGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Newpic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"image to text");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }


        private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }
    private boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== (PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
        return result && result1;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (grantResults.length>0)
                {
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted&&writeStorageAccepted){
                        pickCamera();
                    }
                    else {
                        Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length>0)
                {
                    boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickGallery();
                    }
                    else {
                        Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("Crop")
                        .setCropMenuCropButtonIcon(R.drawable.ic_crop).start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON)
                        .setCropMenuCropButtonIcon(R.drawable.ic_crop).start(this);
            }
        }
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
                Uri resultUri=result.getUri();
                imageView.setImageURI(resultUri);
                BitmapDrawable bitmapDrawable=(BitmapDrawable)imageView.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();
                TextRecognizer recognizer=new TextRecognizer.Builder(getApplicationContext()).build();
                if (!recognizer.isOperational()) {
                    Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Frame frame=new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items=recognizer.detect(frame);
                    StringBuilder sb =new StringBuilder();
                    for (int i=0;i<items.size();i++){
                        TextBlock myitems =items.valueAt(i);
                        sb.append(myitems.getValue());
                        sb.append("\n");
                    }
                    str =sb.toString();
                    addfab.callOnClick();
                    Intent intent=new Intent(MainActivity.this,Result.class);
                    intent.putExtra("mess",str);
                    startActivity(intent);
                    imageView.setVisibility(View.INVISIBLE);
                   // System.out.println(str);
                }
            }
            else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error=result.getError();
                Toast.makeText(this,""+error,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
