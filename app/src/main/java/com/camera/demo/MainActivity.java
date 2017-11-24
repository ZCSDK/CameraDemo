package com.camera.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = (ImageView) findViewById(R.id.camera_demo_imageView);
        findViewById(R.id.btn_take_picture).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtils.selectPicFromLocal(MainActivity.this);
            }
        });

        findViewById(R.id.btn_take_picture_camera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtils.selectPicFromCamera(MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("多媒体返回的结果-------", requestCode + "==========" + resultCode + "========" + data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CameraUtils.REQUEST_CODE_picture) { // 发送本地图片
                if (data != null && data.getData() != null) {
                    imgView.setImageURI(data.getData());//把图片设置到ImageView上
                    Uri selectedImage = data.getData();
                    // 通知handler更新图片
                    Log.i("本地文件路径----", selectedImage.getPath());
                } else {
                    Log.i("本地文件路径----", "没有");
                }
            } else if (requestCode == CameraUtils.REQUEST_CODE_makePictureFromCamera) {
                if (CameraUtils.cameraFile != null && CameraUtils.cameraFile.exists()) {
                    imgView.setImageURI(Uri.fromFile(CameraUtils.cameraFile));
                    Log.i("拍照文件---------", CameraUtils.cameraFile.getPath());
                } else {
                    Log.i("拍照文件---------", "没有");
                }
            }
        }
    }
}