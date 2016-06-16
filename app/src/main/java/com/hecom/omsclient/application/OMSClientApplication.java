package com.hecom.omsclient.application;

import android.app.Application;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

/**
 * Created by zhanggaokai on 16/6/12.
 */
public class OMSClientApplication extends Application {
    private ImageLoader mImageLoader;

    public ImageLoader getImageLoader() {
        if (mImageLoader == null) {
            initImageLoader();
        }
        return mImageLoader;
    }

    private static OMSClientApplication mInstance = null;

    /**
     * @return 返回应用的实例
     * @Title: getInstance
     */
    public static OMSClientApplication getInstance() {
        return mInstance;
    }


    private void initImageLoader() {
        mImageLoader = ImageLoader.getInstance();

        File sdcardDir = Environment.getExternalStorageDirectory();
        //File sdcardCacheDir = new File(sdcardDir, "hecom/imageloader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)/*.denyCacheImageMultipleSizesInMemory()*/
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs()// Remove
                // for
                // release
                // app
                //.discCache(new UnlimitedDiskCache(sdcardCacheDir))// 自定义缓存路径
                .defaultDisplayImageOptions(new DisplayImageOptions.Builder().considerExifParams(true).build()) //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .build();
        mImageLoader.init(config);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
