package com.hecom.omsclient.utils;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.hecom.omsclient.application.OMSClientApplication;

import java.io.File;

/**
 * Created by zhanggaokai on 16/6/16.
 */
public class PathUtils {
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    public static File getFileDirs() {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {

            File result = new File(Environment.getExternalStorageDirectory(),
                    "hecomomsclient/" + "files");
            if (!result.exists()) {
                result.mkdirs();
            }

//            File[] file = ContextCompat.getExternalFilesDirs(OMSClientApplication.getInstance(), null);
//            if (file.length > 0) {
//                return file[0];
//            }
            return result;
        } else {
            return OMSClientApplication.getInstance().getFilesDir();
        }
//        return null;

    }

    public static File getPublicPics() {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            File result = new File(Environment.getExternalStorageDirectory(), "hecomdownload");
            if (!result.exists()) {
                result.mkdirs();
            }
            return result;
        }
        return null;
    }


}
