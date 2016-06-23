package com.hecom.omsclient.utils.tar;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceResponse;

import com.hecom.log.HLog;
import com.hecom.omsclient.BuildConfig;
import com.hecom.omsclient.Constants;
import com.hecom.omsclient.utils.PathUtils;
import com.hecom.omsclient.utils.Tools;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

/**
 * Created by zhanggaokai on 16/6/17.
 */
public class TarCache {


    public static WebResourceResponse getCache(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        //H5选择本地图片http://app/imagePreview?filePath=[param]
//        String app_preview_url = "http://app/imagePreview?filePath=";
//        if (url.startsWith(app_preview_url) && url.length() > app_preview_url.length()) {
//            String picTmpDir = Tools.getPicSaveDir("");
//            String imageFileName = url.substring(app_preview_url.length());
//            try {
//                imageFileName = URLDecoder.decode(imageFileName, "UTF-8");
//                File file = new File(picTmpDir, imageFileName);
//                if (file.exists()) {
//                    InputStream inputStream = new FileInputStream(file);
//                    if (inputStream != null) {
//                        return new WebResourceResponse(Tools.getMIMEType(file), "UTF-8", inputStream);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }


//        if (!ISTARENABLE) {
//            HLog.v(TAG, "设置了tar包不可用,url=" + url);
//            return null;
//        }
//        //插件缓存包中存在此文件
//        PluginTarget pluginTarget = getPlugin(url);
//        if (pluginTarget == null) {
//            HLog.v(TAG, "未有插件可处理此请求,url=" + url);
//            return null;
//        }
//
//
//        //获取CACHE文件保存地址
//        Plugin plugin = pluginTarget.getPlugin();
//        //  File cacheFile = new File(getDir(), plugin.getCacheFile(pluginTarget.filePath));
        InputStream inputStream = getCacheFile(url);
        String localPath = "";
        if (inputStream != null) {
            return new WebResourceResponse(Tools.getMIMEType(localPath), "UTF-8", inputStream);
        }


        return null;
    }


    public static InputStream getCacheFile(String remoteUrl) {
        byte[] content = null;
        if (isTarExists()) {
            content = getCacheFileFromTar(remoteUrl);
            if (content != null) {
                HLog.i("TarCache", "从Tar文件中获取缓存文件成功,remotepath=" + remoteUrl + "  remoteUrl = " + getRelativePath(remoteUrl));
                return new ByteArrayInputStream(content);
            } else {
                HLog.i("TarCache", "从Tar文件中获取缓存文件失败,remoteUrl=" + remoteUrl);
            }
        }
        return null;
    }

    private static byte[] getCacheFileFromTar(String relativeFilePath) {
        relativeFilePath = getRelativePath(relativeFilePath);
        try {
            TarArchiveInputStream tais = new TarArchiveInputStream(new FileInputStream(getTarFile()));
            TarArchiveEntry entry = null;
            byte[] content = null;
            while ((entry = tais.getNextTarEntry()) != null) {
                if (!entry.isDirectory() && relativeFilePath.equalsIgnoreCase(entry.getName())) {
                    content = new byte[tais.available()];
                    int count;
                    int offset = 0;
                    while ((count = tais.read(content, offset, content.length - offset)) != -1) {
                        offset += count;
                        if (offset >= content.length) break;
                    }
                    break;
                }
            }
            tais.close();
            return content;
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static boolean isTarExists() {
        File file = PathUtils.getFileDirs();
        if (file == null) {
            return false;
        }
        File tarFile = new File(file.getAbsolutePath() + File.separator + Constants.TARNAME);
        return tarFile.exists();
    }

    private static File getTarFile() {
        if (isTarExists()) {
            return new File(PathUtils.getFileDirs() + File.separator + Constants.TARNAME);
        }
        return null;
    }

    /**
     * 根据网络地址获取tar中的相对地址
     *
     * @param remotePath
     * @return
     */
    private static String getRelativePath(String remotePath) {
        return remotePath.replaceFirst(BuildConfig.HOST, "app/");
    }
}
