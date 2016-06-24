package com.hecom.omsclient.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.hecom.log.HLog;
import com.hecom.omsclient.Constants;
import com.hecom.omsclient.R;
import com.hecom.omsclient.application.OMSClientApplication;
import com.hecom.omsclient.utils.PathUtils;
import com.hecom.omsclient.utils.Tools;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * Created by zhanggaokai on 16/6/16.
 */
public class DownLoadAPKService extends IntentService {
    NotificationManager mNotificationManager;
    private String apkUrl;
    private String TAG = "DownLoadAPKService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownLoadAPKService(String name) {
        super(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public DownLoadAPKService() {
        super("DownLoadAPKService");
    }

    @Override
    protected void onHandleIntent(Intent args) {
        mNotificationManager = (NotificationManager) OMSClientApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        apkUrl = args.getStringExtra("url");
        RequestParams params = new RequestParams();
//        params
        OMSClientApplication.getSyncHttpClient().post(apkUrl, params, new FileAsyncHttpResponseHandler(DownLoadAPKService.this) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                HLog.i(TAG, "下载过程发生错误,statusCode = " + statusCode);
                updateNotification("下载更新", "下载发生错误", null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                File file = PathUtils.getFileDirs();
                if (file != null) {
                    final File tarLocalFile = new File(file.getAbsolutePath() + File.separator + "OMSClient.apk");
//                    response.renameTo(tarLocalFile);
                    Tools.moveFile(response, tarLocalFile, new Tools.moveFile() {
                        @Override
                        public void success() {
                            HLog.i(TAG, "下载完毕,准备安装");
                            Uri uri = Uri.fromFile(tarLocalFile);//根据File获得安装包的资源定位符
                            Intent installIntent = new Intent(Intent.ACTION_VIEW);//设置Action
                            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//新的Activity会在一个新任务打开，而不是在原先的任务栈
                            installIntent.setDataAndType(uri, "application/vnd.android.package-archive");//设置URI的数据类型
                            startActivity(installIntent);//把打包的Intent传递给startActivity
                            PendingIntent updatePendingIntent = PendingIntent.getActivity(DownLoadAPKService.this, 0, installIntent, 0);
                            updateNotification("下载更新", "下载完成,点击安装", updatePendingIntent);
                            //自动也弹一下
                            installApk(tarLocalFile);
                        }

                        @Override
                        public void failed() {

                        }
                    });

                }
            }

            int lastPercent = 0;

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int current = (int) ((bytesWritten / (totalSize + 0f)) * 100);
                if ((current - lastPercent > 4) && current > 0 && current <= 100) {
                    updateNotification("下载更新", "正在下载" + current + "%", null);
                    lastPercent = current;
                }
            }
        });

    }


    /**
     * 安装apk
     *
     * @param file
     */
    private void installApk(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void updateNotification(String title, String text, PendingIntent pintent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DownLoadAPKService.this);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);
        mBuilder.setAutoCancel(true);
        mBuilder.setSmallIcon(R.drawable.icon);
        if (pintent != null) {
            mBuilder.setContentIntent(pintent);
        }
        mNotificationManager.notify(100, mBuilder.build());
    }
}
