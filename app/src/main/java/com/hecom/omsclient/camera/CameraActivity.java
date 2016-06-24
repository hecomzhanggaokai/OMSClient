package com.hecom.omsclient.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.hecom.location.locators.HcLocation;

import com.hecom.log.HLog;
import com.hecom.omsclient.R;
import com.hecom.omsclient.application.OMSClientApplication;
import com.hecom.omsclient.js.entity.BDPointInfo;
import com.hecom.omsclient.server.BDLocationHandler;
import com.hecom.omsclient.server.BaseHandler;
import com.hecom.omsclient.utils.NameCardRecTask;
import com.hecom.omsclient.utils.PathUtils;
import com.hecom.omsclient.utils.Tools;
import com.hecom.omsclient.widget.AlertDialogWidget;
import com.sosgps.soslocation.SOSLocationService;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


@SuppressLint("NewApi")
public class CameraActivity extends Activity implements RectView.OnClick {
    private static final String TAG = "Camera";
    public static final String POI_INFO = "poiInfo";
    public static final String IMGFILEPATH = "imgfilepath";
    private ImageView takephoto;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private static Camera camera;
    private Camera.AutoFocusCallback mAutoFocusCallback;
    private Parameters mParameters;
    private boolean mFlag = false;
    private ImageView mBtnFlash;
    private ImageView mBtnFront;
    private boolean isFlash;
    private boolean isAutoFocus;
    private ImageView mBtnAutofocus;
    private MyOrientationEventListener orientationEventListener;
    private FrameLayout mCameraPreviewLayout;
    private FrameLayout mConfirmLayout;
    /**
     * 拍完照后的静止页面
     */
    private ImageView mStillImageView;
    private ImageView mConfirmImageView;
    private ImageView mCancelImageView;
    String savePath;
    private Context mContext;
    private RectView mRectView;
    private int mPreviewWidthPicture;
    private Point screenResolution;

    /**
     * The facing of the camera is opposite to that of the screen.
     */
    public static final int CAMERA_FACING_BACK = 0;

    /**
     * The facing of the camera is the same as that of the screen.
     */
    public static final int CAMERA_FACING_FRONT = 1;

    private int cuprentCameraFacing = 0;
    private double longitude = 0;
    private double latitude = 0;

    public int getCuprentCameraFacing() {
        return cuprentCameraFacing;
    }

    /**
     * 最大宽高比差
     */
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    /**
     * 最小预览界面的分辨率
     */
    private static final int MIN_PREVIEW_PIXELS = 1280 * 720;

    /**
     * 最小拍照分辨率
     */
    private static final int MIN_TAKE_PHOTO_PIXELS = 1280 * 720;

    /**
     * 根据不用业务，使用不同的照片存储路径。如果为空，则使用默认的pictmp路径
     */
    private String typeFileFolder = "";

    /**
     * 除名片扫描外的最终图片尺寸到缩放到此值
     */
    private static final int SCALE_TO_WIDTH = 1280;

    /**
     * 是否缩放最终生成的图片
     */
    private boolean mCardScanMode = false;

    /**
     * 名片扫描模式请传入此INTENT，则不缩放拍照片，尽量保证清晰度
     */
    public static final String INTENT_CARD_SCAN_MODE = "CARD_SCAN";

    //名片扫描模式下提示文字渐消动画
    private AlphaAnimation alphaAnimation;

    private ViewGroup group_recognition;
    private SeekBar mProgressBar;

    private TextView txtProgress;
    private TextView mBtnBack, mBtnBackRec;
    private ViewGroup group_scan_tip;
    private ImageView imageview_scan_tip;
    private ImageView still_imageview_common;

    /**
     * 名片识别出错错误后，用户选择手动识别
     */
    public static final String INTENT_HANDLE_INPUT = "HANDLE_INPUT";

//    @Override
//    public String getUserTrackPageName() {
//        if (mCardScanMode) {
//            return "mpsm";
//        } else {
//            return "photo";
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);
        group_recognition = (ViewGroup) findViewById(R.id.group_recognition);
        //挡住click事件
        group_recognition.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mProgressBar = (SeekBar) findViewById(R.id.progressBar);
        mProgressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        txtProgress = (TextView) findViewById(R.id.txtProgress);

        //mProgressBar.setEnabled(false);
        typeFileFolder = this.getIntent().getStringExtra("typeFileFolder");
        mCardScanMode = getIntent().getBooleanExtra(INTENT_CARD_SCAN_MODE, false);
        mRectView = (RectView) findViewById(R.id.rectView1);
        mRectView.setListener(this);
        mCameraPreviewLayout = (FrameLayout) this.findViewById(R.id.camera_preview_framelayout);
        mConfirmLayout = (FrameLayout) this.findViewById(R.id.camera_preview_confirm_or_not);
        mConfirmLayout.setVisibility(View.GONE);
        mStillImageView = (ImageView) findViewById(R.id.still_imageview);
        still_imageview_common = (ImageView) findViewById(R.id.still_imageview_common);
        mConfirmImageView = (ImageView) findViewById(R.id.confirm);
        mCancelImageView = (ImageView) findViewById(R.id.cancel);
        mConfirmImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                UserTrack.click("qd");
                Bundle bundle = new Bundle();
                bundle.putString(IMGFILEPATH, savePath);
                String poiInfo = getPoiInfofromLocation();
                bundle.putString(POI_INFO, poiInfo);
                CameraActivity.this.setResult(RESULT_OK, CameraActivity.this.getIntent().putExtras(bundle));
                CameraActivity.this.finish();
            }
        });
        mCancelImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                UserTrack.click("qx");
                resumeCameraPreview();
            }
        });
        if (!Tools.checkPermission(this, "android.permission.CAMERA")) {
            Toast.makeText(CameraActivity.this, "抱歉，您未授权该应用程序使用相机功能！", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }


        group_scan_tip = (ViewGroup) findViewById(R.id.group_scan_tip);
        imageview_scan_tip = (ImageView) findViewById(R.id.imageview_scan_tip);

        mContext = this;
        PackageManager pm = getPackageManager();
        isFlash = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        isAutoFocus = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
        surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
        takephoto = (ImageView) findViewById(R.id.takephoto);
        takephoto.setOnClickListener(new takephotoListener());
        mBtnFlash = (ImageView) findViewById(R.id.btn_flash);
        mBtnFront = (ImageView) findViewById(R.id.btn_front);
        mBtnBack = (TextView) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                UserTrack.click("fh");
                finish();
            }
        });

        mBtnBackRec = (TextView) findViewById(R.id.btn_back_rec);
        mBtnBackRec.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                UserTrack.click("fh");
//                stopRecTask();
                finish();
            }
        });


/*        mBtnAutofocus = (ImageView) findViewById(R.id.btn_autofocus);
        mBtnAutofocus.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isAutoFocus) {
                    mRectView.showRect();
                    if (camera != null) {
                        camera.autoFocus(mAutoFocusCallback);
                    }
                } else {
                    Toast.makeText(CameraActivity.this, "抱歉，您的手机不支持自动聚焦功能", Toast.LENGTH_SHORT)
                            .show();
                    // takePhoto();
                }
            }
        });*/
        mBtnFlash.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                UserTrack.click("sgd");
                if (!isFlash) {
                    Toast.makeText(CameraActivity.this, "抱歉，您的手机不支持闪光灯功能", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (setFlashLight()) {// 开启闪光灯
                    mBtnFlash.setImageDrawable(mContext.getResources().getDrawable(
                            R.drawable.openlight));
                } else {// 关闭闪光灯
                    mBtnFlash.setImageDrawable(mContext.getResources().getDrawable(
                            R.drawable.closelight));
                }
            }
        });
        mBtnFront.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 切换前后摄像头
                CameraInfo cameraInfo = new CameraInfo();
                int cameraCount = Camera.getNumberOfCameras();
                if (cameraCount == 1) {
                    Toast.makeText(CameraActivity.this, "抱歉，您的手机不支持摄像头切换", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                Camera.getCameraInfo(getCuprentCameraFacing(), cameraInfo);
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                    cuprentCameraFacing = 0;
                } else if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                    cuprentCameraFacing = 1;
                }
                initCamera(surfaceHolder);
            }
        });
        setupViews();
        mAutoFocusCallback = new Camera.AutoFocusCallback() {

            public void onAutoFocus(boolean success, Camera camera) {
            }
        };

        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        screenResolution = new Point(display.getWidth(), display.getHeight());

        if (mCardScanMode) {
            group_scan_tip.setVisibility(View.VISIBLE);
            animateTipWords();
            mBtnFront.setVisibility(View.GONE);
        }

        getLocationInfo();

    }

    private String getPoiInfofromLocation() {
        String poiInfo = "0,0";
        if (longitude != 0 && latitude != 0) {
            poiInfo = longitude + "," + latitude;
        } else {
            // TODO: 2016/5/16 从后台服务获取poiInfo
            HcLocation lastLocation = SOSLocationService.getLastLocation();
            double latitude = lastLocation.getLatitude();
            double longitude = lastLocation.getLongitude();
            long time = lastLocation.getTime();

            long now = System.currentTimeMillis();

            //数据有效且在10分钟内
            if (latitude != 0.0 && longitude != 0.0 && now - time < 1000 * 60 * 10) {
                poiInfo = longitude + "," + latitude;
            } else {
                poiInfo = "0,0";
            }
        }

        return poiInfo;
    }

    private void getLocationInfo() {
        BDLocationHandler locationHandler = new BDLocationHandler(OMSClientApplication.getInstance());
        locationHandler.setmHandlerListener(new BaseHandler.IHandlerListener() {
            @Override
            public <T> void onHandlerListener(T t) {
                Message msg = (Message) t;
                switch (msg.what) {
                    case BDLocationHandler.LOCATION_SUCCESS:
                        BDPointInfo bdPointInfo = (BDPointInfo) msg.obj;
                        if (bdPointInfo != null) {
                            longitude = bdPointInfo.getLongitude();
                            latitude = bdPointInfo.getLatitude();
                        }
                        break;
                    case BDLocationHandler.LOCATION_FAILD:
                        // TODO: 2016/5/7  定位失败提示用户是否再次尝试定位
                        break;
                }
            }
        });
        locationHandler.startLocation();
    }

    private void resumeCameraPreview() {
        mCameraPreviewLayout.setVisibility(View.VISIBLE);
        mConfirmLayout.setVisibility(View.GONE);
        mStillImageView.setVisibility(View.GONE);
        mStillImageView.setImageBitmap(null);
        mBtnBack.setVisibility(View.VISIBLE);
        group_recognition.setVisibility(View.GONE);
        // mParameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
        camera.setParameters(mParameters);
        try {
            camera.startPreview(); // Start Preview
        } catch (Exception e) {
            stopCamera(false);
            Toast.makeText(CameraActivity.this, "您的相机可能损坏", Toast.LENGTH_SHORT).show();
        }

        takephoto.setEnabled(true);
    }

    private void takePhoto() {
        if (camera == null) {
            return;
        }
        camera.setOneShotPreviewCallback(null);
        takephoto.setEnabled(false);
        takePic();
    }

    @Override
    public void onClick() {
        if (isAutoFocus) {
            if (camera != null) {
                try {
                    camera.autoFocus(mAutoFocusCallback);
                } catch (Exception e) {
                }
            }
        } else {
            Toast.makeText(CameraActivity.this, "抱歉，您的手机不支持自动聚焦功能", Toast.LENGTH_SHORT).show();
            // takePhoto();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        surfaceHolder.removeCallback(surfaceCallback);
        if (camera != null) {
            stopCamera(true);
        }
        clearWordAnimation();
//        stopRecTask();
    }

    /**
     * 清除拍照文字提示渐消动画
     */
    private void clearWordAnimation() {
        if (mCardScanMode && alphaAnimation != null && !alphaAnimation.hasEnded()) {
            alphaAnimation.cancel();
        }
    }

    private void stopCamera(boolean isStopPreview) {
        if (camera != null) {
            if (isStopPreview) {
                camera.stopPreview();
            }
            camera.release();
            camera = null;
        }
    }

    private void setupViews() {
        // 加载回调函数
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceCallback);
    }

    class takephotoListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (camera == null) {
                Toast.makeText(CameraActivity.this, "请在设置中启用拍照权限", Toast.LENGTH_SHORT).show();
                return;
            }
            // if (isAutoFocus) {
            // camera.autoFocus(mAutoFocusCallback);
            // } else {
            // Toast.makeText(CameraActivity.this, "抱歉，您的手机不支持自动聚焦功能",
            // Toast.LENGTH_SHORT).show();
            // takePhoto();
            // }
            takePhoto();
        }
    }

    private void takePic() {
        System.gc();
        if (camera == null) {
            return;
        }
        try {
            camera.takePicture(null, null, pictureCallback); // picture
        } catch (Exception e) {
            Toast.makeText(CameraActivity.this, "拍照发生异常请重试", Toast.LENGTH_SHORT).show();
        }
    }

    // Photo call back
    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        // @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.stopPreview();
            // long start = System.currentTimeMillis();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            try {
                Matrix matrix = new Matrix();
                // 旋转图片
                matrix.postRotate(getRotation());
                if (getCuprentCameraFacing() == CAMERA_FACING_FRONT) {
                    // 旋转图片
                    matrix.postScale(1, -1); // 镜像翻转
                }

                //仅名片扫描模式缩放图片
                if (mCardScanMode) {
                    float zoom = (float) SCALE_TO_WIDTH / (float) mPreviewWidthPicture;
                    matrix.postScale(zoom, zoom);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                        matrix, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {


                savePath = PathUtils.getFileDirs().getAbsolutePath() + System.currentTimeMillis() + ".jpg";
                File photoFile = new File(savePath);
                FileOutputStream fos = new FileOutputStream(photoFile);

                int zipTo = 85;
//                if (!mCardScanMode) {
//                    int picQuality = UserInfo.getUserInfo().getPicSaveRank();
//                    if (picQuality == UserInfo.PIC_SAVE_RANK_LOW) {
//                        zipTo = 50;
//                    } else if (picQuality == UserInfo.PIC_SAVE_RANK_MEDIUM) {
//                        zipTo = 70;
//                    }
//                }

//                HLog.d(TAG, "zip qulity=" + zipTo);

                bitmap.compress(CompressFormat.JPEG, zipTo, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {// 重置闪光灯
                String mode = mParameters.getFlashMode();
                if (!TextUtils.isEmpty(mode)) {
                    mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                    camera.setParameters(mParameters);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // mCameraPreviewLayout.setVisibility(View.GONE);


            if (mCardScanMode) {
                mStillImageView.setImageBitmap(bitmap);
//                onPhotoRecognition();
            } else {
                still_imageview_common.setImageBitmap(bitmap);
                mConfirmLayout.setVisibility(View.VISIBLE);
            }

        }
    };

    /**
     * 检查闪光灯模式有效
     *
     * @param mode {@link Parameters#getSupportedFlashModes()}
     * @return
     */
    private boolean isFlashModeValid(String mode) {

        if (!isFlash && (mode != Parameters.FLASH_MODE_OFF)) {
            return false;
        }

        if (camera != null) {
            Parameters parameters = camera.getParameters();
            List<String> flashModes = parameters.getSupportedFlashModes();
            if (flashModes != null && flashModes.contains(mode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 自动闪光灯模式有效
     *
     * @return
     */
    private boolean isFlashModeAutoValid() {
        return isFlashModeValid(Parameters.FLASH_MODE_AUTO);
    }

    private void initCamera(SurfaceHolder holder) {
        try {
            if (camera != null) {
                stopCamera(true);
            }
            try {
                camera = Camera.open(getCuprentCameraFacing()); // Turn on the
            } catch (Exception e) {
            }
            // camera
            if (camera == null) {
                Toast.makeText(CameraActivity.this, "拍照被禁止,请在授权管理软件中启用拍照权限！", Toast.LENGTH_SHORT).show();
                CameraActivity.this.finish();
                return;
            } else {
            }
            Field mNativecontext = camera.getClass().getDeclaredField("mNativeContext");
            mNativecontext.setAccessible(true);
            Object object = mNativecontext.get(camera);
            if (object instanceof Integer) {
                int mCn = (Integer) object;
                if (mCn == 0) {
                    Toast.makeText(CameraActivity.this, "拍照被禁止,请在授权管理软件中启用拍照权限！", Toast.LENGTH_SHORT).show();
                    camera = null;
                    CameraActivity.this.finish();
                    return;
                }
            } else if (object instanceof Long) {
                long mCn = (Long) object;
                if (mCn == 0) {
                    Toast.makeText(CameraActivity.this, "拍照被禁止,请在授权管理软件中启用拍照权限！", Toast.LENGTH_SHORT).show();
                    camera = null;
                    CameraActivity.this.finish();
                    return;
                }
            }

            camera.setPreviewDisplay(holder); // Set Preview

        } catch (Exception e) {
            if (camera != null) {
                HLog.d(TAG, "to release camera");
                stopCamera(false);
            }
        }
        // 设置相机参数
        if (camera == null) {
            return;
        }
        camera.stopPreview();
        mParameters = camera.getParameters();
        orientationEventListener = new MyOrientationEventListener(CameraActivity.this);
        List<Integer> supportedFormats = mParameters.getSupportedPictureFormats();
        if (supportedFormats.contains(ImageFormat.JPEG)) {
            HLog.i("testBitmap", "support JPEG");
            mParameters.setPictureFormat(ImageFormat.JPEG);
        } else if (supportedFormats.contains(ImageFormat.RGB_565)) {
            HLog.i("testBitmap", "support RGB_565");
            mParameters.setPictureFormat(ImageFormat.RGB_565);
        }
        if (camera == null) {
            CameraActivity.this.finish();
            return;
        }
        setCameraDisplayOrientation();
        Point pointPreview = findBestResolution(true);
        Point pointPicture = findBestResolution(false);

        Log.d(TAG, "预览大小=" + pointPreview + ", 拍照大小=" + pointPicture);
        mPreviewWidthPicture = pointPicture.x;
        // 修改surfaceview高度
        // ViewGroup.LayoutParams lp_surface =
        // surfaceView.getLayoutParams();
        // lp_surface.height = pointPreview.x;
        // lp_surface.width = pointPreview.y;
        // surfaceView.setLayoutParams(lp_surface);

        mParameters.setPreviewSize(pointPreview.x, pointPreview.y);
        mParameters.setPictureSize(pointPicture.x, pointPicture.y);

        if (getCuprentCameraFacing() == CAMERA_FACING_BACK) {// 判断后置摄像头，才设置闪光灯模式
            if (isFlashModeAutoValid()) {
                mParameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
            }
        }
        camera.setParameters(mParameters);
        try {
            camera.startPreview(); // Start Preview
        } catch (Exception e) {
            stopCamera(false);
            Toast.makeText(CameraActivity.this, "您的相机可能损坏", Toast.LENGTH_SHORT).show();
        }

        // camera.cancelAutoFocus();
        orientationEventListener.enable();
    }

//    private int displayOrientation;

    private void setCameraDisplayOrientation() {
        CameraInfo info = new CameraInfo();
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        DisplayMetrics dm = new DisplayMetrics();
        Camera.getCameraInfo(getCuprentCameraFacing(), info);
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int displayOrientation;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            displayOrientation = (info.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (info.orientation - degrees + 360) % 360;
        }
        mRotation = displayOrientation;
        camera.setDisplayOrientation(displayOrientation);
    }

    // SurfaceHodler Callback handle to open the camera, off camera and photo
    // size changes
    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            HLog.d(TAG, "surfaceCreated");
            initCamera(holder);
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            HLog.i("testBitmap", "camera surfaceChanged");
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (orientationEventListener != null)
                orientationEventListener.disable();
            if (camera == null) {
                return;
            }
            HLog.d(TAG, "surfaceDestroyed");
            stopCamera(true);
        }
    };


    public boolean setFlashLight() {
        if (camera == null) {
            return false;
        }
        String mode = mParameters.getFlashMode();
        if (mode == null || "".equals(mode)) {
            return false;
        }
        if (mode.equals(Parameters.FLASH_MODE_OFF)) {
            if (isFlashModeAutoValid()) {
                mParameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
                camera.setParameters(mParameters);
            }
            mFlag = true;
        } else {
            mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(mParameters);
            mFlag = false;
        }
        return mFlag;
    }

    private int mRotation = 90;

    private int getRotation() {
        return mRotation;
    }

    private class MyOrientationEventListener extends OrientationEventListener {

        public static final int ORIENTATION_PORTRAIT_NORMAL = 1;
        public static final int ORIENTATION_PORTRAIT_INVERTED = 2;
        public static final int ORIENTATION_LANDSCAPE_NORMAL = 3;
        public static final int ORIENTATION_LANDSCAPE_INVERTED = 4;

        private int type = ORIENTATION_PORTRAIT_NORMAL;

        public MyOrientationEventListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            try {
                if (orientation == ORIENTATION_UNKNOWN)
                    return;

                int newType = ORIENTATION_PORTRAIT_NORMAL;
                if (orientation >= 315 || orientation < 45) {
                    newType = ORIENTATION_PORTRAIT_NORMAL;
                } else if (orientation < 315 && orientation >= 225) {
                    newType = ORIENTATION_LANDSCAPE_NORMAL;
                } else if (orientation < 225 && orientation >= 135) {
                    newType = ORIENTATION_PORTRAIT_INVERTED;
                } else if (orientation < 135 && orientation > 45) {
                    newType = ORIENTATION_LANDSCAPE_INVERTED;
                }
                if (type != newType) {
                    type = newType;
                    CameraInfo info = new CameraInfo();
                    Camera.getCameraInfo(getCuprentCameraFacing(), info);
                    orientation = (orientation + 45) / 90 * 90;
                    if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                        mRotation = (info.orientation - orientation + 180) % 360;
                    } else { // back-facing camera
                        mRotation = (info.orientation + orientation) % 360;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void enable() {
            super.enable();
            type = ORIENTATION_PORTRAIT_NORMAL;
        }

        @Override
        public void disable() {
            super.disable();
            type = ORIENTATION_PORTRAIT_NORMAL;
        }
    }

    /**
     * 找出最适合的预览界面分辨率
     * 要求和屏幕比例一致，预览先大尺寸，拍照选小尺寸
     */
    private Point findBestResolution(boolean preview) {
        Camera.Size defaultResolution;
        List<Camera.Size> rawSupportedSizes;
        int minSizeLimit;
        if (preview) {
            defaultResolution = mParameters.getPreviewSize();
            rawSupportedSizes = mParameters.getSupportedPreviewSizes();
            minSizeLimit = MIN_PREVIEW_PIXELS;
        } else {
            defaultResolution = mParameters.getPictureSize();
            rawSupportedSizes = mParameters.getSupportedPictureSizes();
            minSizeLimit = MIN_TAKE_PHOTO_PIXELS;
        }

        if (rawSupportedSizes != null && rawSupportedSizes.size() > 0) {
            // 按照分辨率面积从大到小排序
            List<Camera.Size> supportedResolutions = new ArrayList<Camera.Size>(
                    rawSupportedSizes);
            Collections.sort(supportedResolutions, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size a, Camera.Size b) {
                    int aPixels = a.height * a.width;
                    int bPixels = b.height * b.width;
                    if (bPixels < aPixels) {
                        return -1;
                    }
                    if (bPixels > aPixels) {
                        return 1;
                    }
                    return 0;
                }
            });

            // 移除不符合条件的分辨率
            double screenAspectRatio = (double) screenResolution.x / (double) screenResolution.y;
            Iterator<Camera.Size> it = supportedResolutions.iterator();
            while (it.hasNext()) {
                Camera.Size supportedResolution = it.next();
                int width = supportedResolution.width;
                int height = supportedResolution.height;

                // 移除低于下限的分辨率，尽可能取高分辨率
                if (width * height < minSizeLimit) {
                    it.remove();
                    continue;
                }

                // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
                // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
                // 因此这里要先交换然preview宽高比后在比较
                boolean isCandidatePortrait = width > height;
                int maybeFlippedWidth = isCandidatePortrait ? height : width;
                int maybeFlippedHeight = isCandidatePortrait ? width : height;
                double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
                double distortion = Math.abs(aspectRatio - screenAspectRatio);
                if (distortion > MAX_ASPECT_DISTORTION) {
                    it.remove();
                }
            }

            // 如果没有找到合适的，并且还有候选的像素，预览模式用最大模式，拍照用最小(通常是720p)
            if (!supportedResolutions.isEmpty()) {
                Camera.Size targetResolution = supportedResolutions.get(
                        preview ? 0 : (supportedResolutions.size() - 1));
                return new Point(targetResolution.width, targetResolution.height);
            }
        }
        // 没有找到合适的，就返回默认的
        return new Point(defaultResolution.width,
                defaultResolution.height);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CameraActivity.this.setResult(RESULT_OK);
            CameraActivity.this.finish();
        }
        return false;
    }


    private void animateTipWords() {
        alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setStartOffset(4000);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageview_scan_tip.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageview_scan_tip.setAnimation(alphaAnimation);
        alphaAnimation.startNow();
    }

//    private NameCardRecTask mRecognitionTask;

//    private void stopRecTask() {
//        if (mRecognitionTask != null) {
//            mRecognitionTask.cancel();
//            mRecognitionTask = null;
//        }
//    }

//    private void onPhotoRecognition() {
//        group_recognition.setVisibility(View.VISIBLE);
//        stopRecTask();
//        clearWordAnimation();
//        mBtnBack.setVisibility(View.GONE);
//        mRecognitionTask = new NameCardRecTask();
//        mRecognitionTask.startTask(savePath, new NameCardRecTask.OnCallback() {
//            @Override
//            public void onError(NameCardRecTask.ERROR_TYPE error) {
//                switch (error) {
//                    case NETWORK:
//                        HLog.d(TAG, "网络不给力，名片识别失败");
//                        showRecErrorDialog();
//                        break;
//                    case FAIL:
//                        HLog.d(TAG, "名片识别失败,fail");
//                        showRecErrorDialog();
//                        break;
//                    case TIMEOUT:
//                        HLog.d(TAG, "名片识别失败，超时");
//                        showRecErrorDialog();
//                        break;
//                    default:
//                        HLog.d(TAG, "名片识别失败");
//                        showRecErrorDialog();
//                        break;
//                }
//            }
//
//            @Override
//            public void onSuccess(String vcf) {
//                //CamCard.showResult(CameraActivity.this, vcf, savePath, 0, 0);
//                Bundle bundle = new Bundle();
//                bundle.putString("imgfilepath", savePath);
//                bundle.putString("vcf", vcf);
//                bundle.putBoolean(INTENT_HANDLE_INPUT, false);
//                setResult(RESULT_OK, getIntent().putExtras(bundle));
//                finish();
//            }
//
//            @Override
//            public void onProgress(int progress) {
//                mProgressBar.setProgress(progress);
//                txtProgress.setText("正在为你识别名片... " + progress + "%");
//            }
//        });
//
//
//    }


    /**
     * 扫描失败，选择手动输入
     */
    private void handleInput() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(INTENT_HANDLE_INPUT, true);
        setResult(RESULT_OK, getIntent().putExtras(bundle));
        finish();
    }

    /**
     * 显示扫描失败对话框
     */
    private void showRecErrorDialog() {
//        final String[] options = new String[]{"手动输入", "重拍", "关闭"};
//        AlertDialogWidget.showSelectSingle(this, "无法识别该名片", Arrays.asList(options),
//                new AlertDialogWidget.OnSelectListener() {
//                    @Override
//                    public void onSelect(int position) {
//                        switch (position) {
//                            case 0:
//                                UserTrack.click("sdsr");
//                                handleInput();
//                                break;
//                            case 1:
//                                UserTrack.click("cp");
//                                resumeCameraPreview();
//                                break;
//                            case 2:
//                                UserTrack.click("gb");
//                                finish();
//                                break;
//                        }
//                    }
//                });
    }
}
