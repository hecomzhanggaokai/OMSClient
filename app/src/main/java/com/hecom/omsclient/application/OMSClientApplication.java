package com.hecom.omsclient.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.text.TextUtils;

import com.growingio.android.sdk.collection.GrowingIO;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by zhanggaokai on 16/6/12.
 */
public class OMSClientApplication extends Application {
    private ImageLoader mImageLoader;
    private static AsyncHttpClient client;
    private static SyncHttpClient syncHttpClient;

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

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)/*.denyCacheImageMultipleSizesInMemory()*/
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs()// Remove
                .defaultDisplayImageOptions(new DisplayImageOptions.Builder().considerExifParams(true).build()) //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .build();
        mImageLoader.init(config);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        GrowingIO.startTracing(this, "8e9d847313d96594");
        GrowingIO.setScheme("growing.2cc36af64f48020d");
        String channel = getChannel(this);
        if (TextUtils.isEmpty(channel)) {
            channel = "other-market";
        }
        GrowingIO.getInstance().setChannel(channel);
    }

    public static AsyncHttpClient getHttpClient() {

        if (client == null) {
            client = new AsyncHttpClient();
            client.setMaxRetriesAndTimeout(0, 15000);
        }
        return client;
    }

    public static SyncHttpClient getSyncHttpClient() {
        if (syncHttpClient == null) {
            syncHttpClient = new SyncHttpClient();
            syncHttpClient.setMaxRetriesAndTimeout(3, 300000);
        }
        return syncHttpClient;
    }

    /**
     * 读取CHANNEL渠道号
     * TODO : 异步执行
     *
     * @param context
     * @return
     */
    public static String getChannel(Context context) {
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String ret = null;
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.equals("META-INF/channel")) {
                    InputStream inputStream = zipfile.getInputStream(entry);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    ret = reader.readLine(); // 读取第一行
                    reader.close();
                    inputStream.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }
}
