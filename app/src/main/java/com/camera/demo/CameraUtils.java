package com.camera.demo;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

class CameraUtils {

    static int REQUEST_CODE_picture = 701; // 获取照片列表
    static int REQUEST_CODE_makePictureFromCamera = 702;//直接照相获取图片
    private static int SOBOT_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 192;
    private static boolean has_write_external_storage_permission = false;
    static File cameraFile;

    /**
     * 通过照相上传图片
     */
    static void selectPicFromCamera(Activity act) {
        Log.i("多媒体返回的结果-------", "selectPicFromCamera------------------1");
        if (!isExitsSdcard()) {
            Toast.makeText(act.getApplicationContext(), "没有内存卡", Toast.LENGTH_SHORT).show();
            return;
        }

        int SOBOT_CAMERA_REQUEST_CODE = 191;
        boolean has_camera_permission = checkPermission(act, Manifest.permission.CAMERA, SOBOT_CAMERA_REQUEST_CODE);
        has_write_external_storage_permission = checkPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE, SOBOT_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        if (!has_camera_permission || !has_write_external_storage_permission) {
            return;
        }
        Log.i("多媒体返回的结果-------", "selectPicFromCamera------------------2");
        openCamera(act);
    }

    /**
     * 从图库获取图片
     */
    static void selectPicFromLocal(Activity act) {
        if (Build.VERSION.SDK_INT >= 23) {
            has_write_external_storage_permission = checkPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE, SOBOT_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            if (!has_write_external_storage_permission) {
                return;
            }
        }

        openSelectPic(act);
    }

    /**
     * 打开选择图片界面
     */
    private static void openSelectPic(Activity act) {
        if (act == null) {
            return;
        }
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        try {
            act.startActivityForResult(intent, REQUEST_CODE_picture);
        } catch (Exception e) {
            Toast.makeText(act.getApplicationContext(), "无法打开相册，请检查相册是否开启", Toast.LENGTH_SHORT);
        }
    }

    /**
     * 打开相机
     */
    private static File openCamera(Activity act) {
        Log.i("多媒体返回的结果-------", "openCamera------------------1");
        if (act == null) {
            return null;
        }

        String path = getSDCardRootPath() + "/" + getApplicationName(act.getApplicationContext()) + "/" + System.currentTimeMillis() + ".jpg";
        // 创建图片文件存放的位置
        cameraFile = new File(path);
        @SuppressWarnings("unused")
        boolean mkdirs = cameraFile.getParentFile().mkdirs();
        Uri uri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
            uri = act.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues);
        } else {
            uri = Uri.fromFile(cameraFile);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri);
        act.startActivityForResult(intent, REQUEST_CODE_makePictureFromCamera);
        Log.i("多媒体返回的结果-------", "openCamera------------------2" + "cameraFile------" + cameraFile.getAbsolutePath());
        return cameraFile;
    }

    /**
     * 获取sdcard 外部存储位置
     */
    private static String getSDCardRootPath() {
        String sdCard = Environment.getExternalStorageDirectory().toString();
        Log.i("tag", "手机外部存储地址：" + sdCard);
        return sdCard;
    }

    /**
     * 获取应用的名称
     */
    private static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = "";
        if (applicationInfo != null) {
            applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        }
        return applicationName;
    }

    /**
     * 检测Sdcard是否存在
     */
    private static boolean isExitsSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 检查6.0 动态权限
     *
     * @param act  activity
     * @param type 权限类型
     * @param code 返回码
     * @return 是否有权限
     */
    private static boolean checkPermission(Activity act, String type, int code) {
        boolean isPermission;
        if (getTargetSdkVersion(act.getApplicationContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(act, type)
                    != PackageManager.PERMISSION_GRANTED) {
                isPermission = false;
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

    private static int getTargetSdkVersion(Context context) {
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