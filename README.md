# MainActivity 图片选择和拍照

使用方法

在build.gradle文件中添加远程依赖：

compile 'com.sobot.camera:camera_tools:1.2'

在MainActivity的布局文件中添加以下view

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:weightSum="2">

        <Button
            android:id="@+id/btn_take_picture"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="相册" />

        <Button
            android:id="@+id/btn_take_picture_camera"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="相机" />
    </LinearLayout>

    <TextView
        android:text="上面从相册或者相机获取的图片会显示在线面ImageView上"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

    <ImageView
        android:id="@+id/camera_demo_imageView"
        android:layout_width="match_parent"
        android:layout_height="200dp" />

在MainActivity类中添加以下五个属性

    private ImageView imgView;//用来显示  获取到的图片或者拍照的图片

    protected boolean has_camera_permission = false;//是否有使用相机权限
    protected boolean has_write_external_storage_permission = false;//是否有存储权限

    //申请权限的code
    int SOBOT_CAMERA_REQUEST_CODE = 191;
    int SOBOT_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 192;

在MainActivity的onCreate方法中添加以下代码

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

在MainActivity类中添加以下两个方法

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

    //获取当前版本targetSdkVersion
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

在MainActivity类中重写onActivityResult(int requestCode, int resultCode, Intent data)

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
