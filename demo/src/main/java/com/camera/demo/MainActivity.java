package com.camera.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.camera.library.CameraUtils;

public class MainActivity extends Activity {

    private ImageView imgView;

    protected boolean has_camera_permission = false;
    protected boolean has_write_external_storage_permission = false;

    //申请权限的code
    int SOBOT_CAMERA_REQUEST_CODE = 191;
    int SOBOT_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 192;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        has_camera_permission = checkPermission(this, Manifest.permission.CAMERA, SOBOT_CAMERA_REQUEST_CODE);
        has_write_external_storage_permission = checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, SOBOT_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        if (!has_camera_permission || !has_write_external_storage_permission) {
            return;
        }

        imgView = findViewById(R.id.camera_demo_imageView);
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

    /**
     * 检查6.0 动态权限
     * @param act activity
     * @param type 权限类型
     * @param code 返回码
     * @return 是否有权限
     */
    public static boolean checkPermission(Activity act, String type, int code){
        boolean isPermission;
        if(getTargetSdkVersion(act.getApplicationContext()) >= 23){
            if (ContextCompat.checkSelfPermission(act, type)
                    != PackageManager.PERMISSION_GRANTED) {
                isPermission=false;
                //申请权限
                ActivityCompat.requestPermissions(act, new String[]{type}, code);
            } else {
                isPermission = true;
            }
        } else {
            isPermission = true;
        }
        return isPermission;
    }

    public static int getTargetSdkVersion(Context context){
        int targetSdkVersion = 0;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return targetSdkVersion;
    }
}