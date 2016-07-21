package com.hecom.omsclient.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

//import com.easemob.chat.NotificationCompat;
//import com.hecom.application.SOSApplication;
//import com.hecom.config.Config;
//import com.hecom.config.CustomerGuideHelper;
//import com.hecom.entconfig.EntConfigLogicManager;
//import com.hecom.fragment.BaseMainFragment;
//import com.hecom.im.dao.IMFriend;
//import com.hecom.logs.HLog;
//import com.hecom.management.R;
//import com.hecom.order.OrderConfirmActivity;
//import com.hecom.user.dataPersistence.UserInfo;
//import com.hecom.util.json.JSONArray;
//import com.hecom.util.json.JSONException;
//import com.hecom.util.json.JSONObject;
//import com.tencent.bugly.crashreport.CrashReport;

import com.hecom.log.HLog;
import com.hecom.omsclient.Constants;
import com.hecom.omsclient.application.OMSClientApplication;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.onekeyshare.OnekeyShare;
//import cn.sharesdk.onekeyshare.OnekeyShareTheme;

public class Tools {
    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
//            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }


    public static void moveFile(final File from, final File to, final moveFile listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (to.exists()) {
                    to.delete();
                }

                FileInputStream fileInputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    fileInputStream = new FileInputStream(from);
                    fileOutputStream = new FileOutputStream(to);
                    byte[] tempbytes = new byte[1024];
                    int byteread = 0;
                    while ((byteread = fileInputStream.read(tempbytes)) != -1) {
                        fileOutputStream.write(tempbytes, 0, byteread);
                    }
                    listener.success();
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.failed();
                } finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    from.delete();
                    HLog.i("Tools", "删除缓存文件");
                }
            }
        }).start();


    }

    public static interface moveFile {

        void success();

        void failed();

    }

    /**
     * 获得保存图片的文件夹路径
     * <p>
     * 根据不用业务，使用pictmp/folderType/指定的照片存储路径。如果为空，则使用默认的pictmp/路径
     * </p>
     * 建议在启动CamerActivity的时候，传入"typeFileFolder"参数,来制定不同业务，使用不同的图片文件夹
     *
     * @param folderType 不同的业务文件夹路径
     * @return
     */
    public static String getPicSaveDir(String folderType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSdRootPath());
        stringBuilder.append("/");
        stringBuilder.append("pictmp/");
        if (!TextUtils.isEmpty(folderType)) {
            stringBuilder.append(folderType);
            stringBuilder.append("/");
        }
        String fileDir = stringBuilder.toString();
        File filePth = new File(fileDir);
        if (!filePth.exists()) {
            filePth.mkdirs();
        }
        return fileDir;
    }

    /**
     * 取SD卡路径，不带最后分隔符的; 如没有sd卡，返回data/data/files路径; 如有异常，返回空
     *
     * @return
     */
    public static String getSdRootPath() {
        File sdDir = null;
        try {
            boolean sdCardExist = Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory(); // 获取根目录
            } else {
                sdDir = OMSClientApplication.getInstance().getFilesDir();
            }
            if (sdDir != null) {
                return sdDir.getPath();
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }

    public static String getMIMEType(String var0) {
        String var1 = "";
        String var2 = var0.substring(var0.lastIndexOf(".") + 1, var0.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var2);
        return var1;
    }

    public static boolean isSplashImgNeedDownLoad(String md5) {
        File file = PathUtils.getFileDirs();
        if (file == null) {
            return false;
        }
        if (isSplashImgExists()) {
            File splashFile = new File(file.getAbsolutePath() + File.separator + Constants.SPLASHIMGNAME);
            return !isLocalAndServerMd5Same(md5, splashFile);
        } else {
            return true;
        }
    }


    /**
     * 比较本地图片文件与服务端获得的md5是否一致
     */
    private static boolean isLocalAndServerMd5Same(String serverStr, File localFilePath) {
        String local_Md5 = "";
        try {
            local_Md5 = Tools.getMd5ByFile(localFilePath.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(local_Md5) || TextUtils.isEmpty(serverStr)) {
            return false;
        } else {
            if (local_Md5.equalsIgnoreCase(serverStr)) {
                return true;
            } else {
                return false;
            }
        }
    }


    public static boolean isSplashImgExists() {
//        String localSplashImgUrl = SharedPreferencesUtils.get(Constants.SPLASHURLKEY);
//        if (TextUtils.isEmpty(localSplashImgUrl)) {
//            return false;
//        }
        File file = PathUtils.getFileDirs();
        if (file == null) {
            return false;
        }
        File splashFile = new File(file.getAbsolutePath() + File.separator + Constants.SPLASHIMGNAME);
        return splashFile.exists();
    }

    public static boolean isTarExists() {
//        String localSplashImgUrl = SharedPreferencesUtils.get(Constants.SPLASHURLKEY);
//        if (TextUtils.isEmpty(localSplashImgUrl)) {
//            return false;
//        }
        File file = PathUtils.getFileDirs();
        if (file == null) {
            return false;
        }
        File splashFile = new File(file.getAbsolutePath() + File.separator + Constants.TARNAME);
        return splashFile.exists();
    }

    public static int VersionCompare(String s1, String s2) {
        if (s1 == null || s1.length() == 0) {
            s1 = null;
        }
        if (s2 == null || s2.length() == 0) {
            s2 = null;
        }
        if (s1 == null && s2 == null)
            return 0;
        else if (s1 == null)
            return -1;
        else if (s2 == null)
            return 1;
        String[]
                arr1 = s1.split("[^a-zA-Z0-9]+"),
                arr2 = s2.split("[^a-zA-Z0-9]+");

        int i1, i2, i3;

        for (int ii = 0, max = Math.min(arr1.length, arr2.length);
             ii <= max; ii++) {
            if (ii == arr1.length)
                return ii == arr2.length ? 0 : -1;
            else if (ii == arr2.length)
                return 1;

            try {
                i1 = Integer.parseInt(arr1[ii]);
            } catch (Exception x) {
                i1 = Integer.MAX_VALUE;
            }

            try {
                i2 = Integer.parseInt(arr2[ii]);
            } catch (Exception x) {
                i2 = Integer.MAX_VALUE;
            }

            if (i1 != i2) {
                return i1 - i2;
            }

            i3 = arr1[ii].compareTo(arr2[ii]);

            if (i3 != 0)
                return i3;
        }

        return 0;
    }

    public static void main(String[] args) {
        System.out.println(VersionCompare("", "0.1"));
        System.out.println(VersionCompare("0.0.1", "0.1"));
        System.out.println(VersionCompare("2.0.1", "2.1"));
        System.out.println(VersionCompare("2.1.1", "2.1.0"));
        System.out.println(VersionCompare("2.0.1", "2.1"));
        System.out.println(VersionCompare("2.0.1", "2.1"));
        System.out.println(VersionCompare("3.0.1", "2.1.9.e.5.454"));
        System.out.println(VersionCompare("3.0.10", "3.0.11"));

        System.out.println(VersionCompare("3.0.a", "3.6.1b"));
        System.out.println(VersionCompare("3.0.1a", "3.6.1b"));
        System.out.println(VersionCompare("3.0.1a", "3.6.1b"));
        System.out.println(VersionCompare("123", "234"));
        System.out.println(VersionCompare("", "234"));
        System.out.println(VersionCompare("123", "234"));
        System.out.println(VersionCompare("00.22..1", "00.22..2"));
        System.out.println(VersionCompare("6.0.0.build123", "6.0.1.build123"));
    }


//    public static boolean needDownLoad(String remoteSplashUrl) {
//        //存储空间都没有,当然不用再去下载了......
//        File file = PathUtils.getFileDirs();
//        if (file == null) {
//            return false;
//        }
//
//        if (!isSplashImgExists()) {
//            return true;
//        }
//
//        String localSplashImgUrl = SharedPreferencesUtils.get(Constants.SPLASHURLKEY);
//        return !(remoteSplashUrl.equals(localSplashImgUrl));
//    }


    //

    /**
     * 获取本地文件的md5值
     *
     * @param filePath
     * @return
     * @throws FileNotFoundException
     */
    public static String getMd5ByFile(String filePath) throws FileNotFoundException {
        String value = null;
        File file = new File(filePath);
        FileInputStream in = new FileInputStream(file);
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
                    file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    //
//	/**
//	 * 获得账号. 获取要提交的设备号 3.0用户返回deviceID 4.0用户返回账户
//	 *
//	 * @param context
//	 * @return
//	 */
//	public static String getAccount(Context context) {
//		return Config.getUniqueUserId();
//	}
//
//	/**
//	 * 返回特殊的标示
//	 *
//	 * @param context
//	 * @return account_time
//	 */
//	public static String getCode(Context context) {
//		return Config.getUniqueUserId() + "_" + new Date().getTime();
//	}
//
//	/**
//	 * 组装发送失败的数据
//	 *
//	 * @param context
//	 * @param array
//	 * @return
//	 */
//	public static JSONObject getInstallStateJson(Context context, JSONArray array) {
//		if (array == null || array.length() == 0)
//			return null;
//		JSONObject json = new JSONObject();
//		try {
//			json.put("type", "remoteInstall");
////			json.put("deviceId", PrefUtils.getCurrentAccount());
//			json.put("data", array);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return json;
//	}
//
//	/**
//	 * 读取文件内容
//	 *
//	 * @param path
//	 * @param name
//	 * @return
//	 */
//	public static JSONArray readJson(String path, String name) {
//		JSONArray array = new JSONArray();
//		File file = new File(path + name);
//		if (file.isFile()) {
//			BufferedReader reader = null;
//			try {
//				reader = new BufferedReader(new FileReader(file));
//				String tempString = null;
//				int line = 1;
//				// 一次读入一行，直到读入null为文件结束
//				while ((tempString = reader.readLine()) != null) {
//					// 显示行号
//					System.out.println("line " + line + ": " + tempString);
//					if (!tempString.trim().equals("")) {
//						try {
//							JSONObject json = new JSONObject(tempString);
//							array.put(json);
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//					line++;
//				}
//				reader.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				if (reader != null) {
//					try {
//						reader.close();
//					} catch (IOException e1) {
//					}
//				}
//			}
//		}
//		return array;
//	}
//
//	/**
//	 * 删除指定文件
//	 *
//	 * @param name
//	 */
//	public static void deleteFile(String name) {
//		File file = new File(name);
//		if (file.isAbsolute())
//			file.delete();
//	}
//
//	/**
//	 * 删除文件夹里面的内容
//	 *
//	 * @param folderName
//	 * @return
//	 */
//	public static boolean delAllFile(String folderName) {
//		try {
//			File file = new File(folderName);
//			if (!file.exists()) {
//				return false;
//			}
//			if (!file.isDirectory()) {
//				return false;
//			}
//			boolean flag = false;
//			String[] tempList = file.list();
//			File temp;
//			for (int i = 0; i < tempList.length; i++) {
//				if (folderName.endsWith(File.separator)) {
//					temp = new File(folderName + tempList[i]);
//				} else {
//					temp = new File(folderName + File.separator + tempList[i]);
//				}
//				if (temp.isFile()) {
//					temp.delete();
//				}
//				if (temp.isDirectory()) {
//					delAllFile(folderName + "/" + tempList[i]);// 先删除文件夹里面的文件
//					delFolder(folderName + "/" + tempList[i]);// 再删除空文件夹
//					flag = true;
//				}
//			}
//			return flag;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//	 * 删除整个文件夹
//	 *
//	 * @param folderPath
//	 */
//	public static void delFolder(String folderPath) {
//		try {
//			delAllFile(folderPath); // 删除完里面所有内容
//			String filePath = folderPath;
//			filePath = filePath.toString();
//			File myFilePath = new File(filePath);
//			myFilePath.delete(); // 删除空文件夹
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * @param time
//	 */
//	public static boolean configWorkingDays(String time) {
//		String[] workTime = new String[]{};
//		try {
//			workTime = time.split("-");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		boolean isWorkingDays = true;
//		if (!EntConfigLogicManager.isWorkDay()) {
//			isWorkingDays = false;
//		} else {
//			isWorkingDays = isWorkTime(workTime[0], workTime[1], System.currentTimeMillis());
//		}
//		return isWorkingDays;
//	}
//
//	/**
//	 * Is it working time
//	 *
//	 * @param beginTime
//	 * @param endTime
//	 * @param currentTimeMillis
//	 * @return if true, means currentTime is on workTime
//	 */
//	private static boolean isWorkTime(String beginTime, String endTime, long currentTimeMillis) {
//		SimpleDateFormat sdf = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
//		sdf.applyPattern("HH:mm:ss");
//		String currentTime = sdf.format(currentTimeMillis);
//		DateFormat df = new SimpleDateFormat("HH:mm:ss");
//		Calendar c1 = Calendar.getInstance();
//		Calendar c2 = Calendar.getInstance();
//		Calendar c3 = Calendar.getInstance();
//		try {
//			c1.setTime(df.parse(beginTime));
//			c2.setTime(df.parse(endTime));
//			c3.setTime(df.parse(currentTime));
//		} catch (ParseException e) {
//			return false;
//		}
//		int result = c1.compareTo(c2);
//		if (result == 0) {
//			// begin time equal end time
//			return false;
//		} else if (result < 0) {
//			// beginTime less than endTime
//			// beginTime equals currentTime
//			int result1 = c1.compareTo(c3);
//			// endTime equals currentTime
//			int result2 = c2.compareTo(c3);
//			if (result1 < 0 && result2 > 0) {
//				// beginTime less than currentTime
//				return true;
//			} else {
//				return false;
//			}
//		} else {
//			return false;
//		}
//	}
//
//	/**
//	 * 获取apk文件版本号
//	 *
//	 * @param context
//	 * @param FilePath
//	 * @return
//	 */
//	public static String getApkVersion(Context context, String FilePath) {
//		PackageManager pm = context.getPackageManager();
//		PackageInfo info = pm.getPackageArchiveInfo(FilePath, PackageManager.GET_ACTIVITIES);
//		// 得到版本信息
//		return (info == null) ? "" : info.versionName;
//	}
//
//	/**
//	 * @param context
//	 * @param pxValue
//	 * @return
//	 */
//	public static int px2dip(Context context, float pxValue) {
//		final float scale = context.getResources().getDisplayMetrics().density;
//		return (int) (pxValue / scale + 0.5f);
//	}
//
//	/**
//	 * @param context
//	 * @param pxValue
//	 * @return
//	 */
//	public static int px2sp(Context context, float pxValue) {
//		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
//		return (int) (pxValue / fontScale + 0.5f);
//	}
//
//	/**
//	 * @param context
//	 * @param dipValue
//	 * @return
//	 * @Description: Dip转Px
//	 */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
//
//	/**
//	 * 获取照片旋转度数
//	 *
//	 * @param filepath
//	 * @return
//	 */
//	private static int getExifOrientation(String filepath) {
//		int degree = 0;
//		ExifInterface exif = null;
//		try {
//			exif = new ExifInterface(filepath);
//		} catch (IOException ex) {
//			HLog.e("tools", "cannot read exif", ex);
//		}
//		if (exif != null) {
//			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
//			if (orientation != -1) {
//				switch (orientation) {
//					case ExifInterface.ORIENTATION_ROTATE_90:
//						degree = 90;
//						break;
//					case ExifInterface.ORIENTATION_ROTATE_180:
//						degree = 180;
//						break;
//					case ExifInterface.ORIENTATION_ROTATE_270:
//						degree = 270;
//						break;
//				}
//			}
//		}
//		return degree;
//	}
//
//	public static void commpressImage(String filename) {
//		File file = new File(filename);
//		// byte[] data = getBytes(filename);
//		// Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//
//		Bitmap bitmap = getImageByPath(filename);
//		try {
//			FileOutputStream fos = new FileOutputStream(file);
//		    /*
//             * 位图格式为JPEG 参数二位 0-100 的数值，100为最大值，表示无损压缩 参数三传入一个输出流对象，将图片数据输出到流中
//			 */
//			bitmap.compress(CompressFormat.JPEG, 80, fos);
//			fos.flush();
//			fos.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			bitmap.recycle();
//		}
//	}
//
//	public static void setPictureDegreeZero(String srcPath) {
//		File file = new File(srcPath);
//
//		int degree = 90;
//		if (degree != 0 && degree < 360) {
//			Matrix matrix = new Matrix();
//			matrix.postRotate(degree);
//			BitmapFactory.Options newOpts = new BitmapFactory.Options();
//			newOpts.inJustDecodeBounds = false;
//			Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
//			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
//					matrix, true);
//			try {
//				FileOutputStream fos = new FileOutputStream(file);
//                /*
//                 * 位图格式为JPEG 参数二位 0-100 的数值，100为最大值，表示无损压缩
//				 * 参数三传入一个输出流对象，将图片数据输出到流中
//				 */
//				bitmap.compress(CompressFormat.JPEG, 50, fos);
//				fos.flush();
//				fos.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				bitmap.recycle();
//			}
//		}
//	}
//
//	/**
//	 * 改变拍完生成的照片的尺寸
//	 *
//	 * @param srcPath
//	 */
//	public static Bitmap getImageByPath(String srcPath) {
//		BitmapFactory.Options newOpts = new BitmapFactory.Options();
//		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
//		newOpts.inJustDecodeBounds = true;
//		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
//
//		newOpts.inJustDecodeBounds = false;
//		int w = newOpts.outWidth;
//		int h = newOpts.outHeight;
//		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//		float hh = 800f;// 这里设置高度为800f
//		float ww = 600f;// 这里设置宽度为480f
//		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//		int zoom = 1;// zoom=1表示不缩放
//		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
//			zoom = (int) (newOpts.outWidth / ww);
//		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
//			zoom = (int) (newOpts.outHeight / hh);
//		}
//		if (zoom <= 0)
//			zoom = 1;
//		newOpts.inSampleSize = zoom;// 设置缩放比例
//		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//		// 计算角度,更改照片
//		int degree = getExifOrientation(srcPath);
//		if (degree != 0) {
//			Matrix matrix = new Matrix();
//			matrix.postRotate(degree);
//			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
//					matrix, true);
//		}
//		return bitmap;
//	}
//
//	/**
//	 * 获取文件大小
//	 *
//	 * @param filePath
//	 * @return -1表示不存在
//	 */
//	public static long getFileSize(String filePath) {
//		File file = new File(filePath);
//		if (file.exists() && file.isFile()) {
//			return file.length();
//		}
//		return -1;
//	}
//
//	/**
//	 * float类型数字小数点后截取位数
//	 *
//	 * @param f   准备处理数字
//	 * @param num 截取位数
//	 * @return
//	 */
//	public static float getFloatRound(float f, int num) {
//		BigDecimal b = new BigDecimal(f);
//		float f1 = b.setScale(num, BigDecimal.ROUND_HALF_UP).floatValue();
//		return f1;
//	}
//
//	public static boolean getAllFileSize(String path, long[] totalSize) {
//		boolean flag = false;
//		File file = new File(path);
//		if (!file.exists()) {
//			return flag;
//		}
//		if (!file.isDirectory()) {
//			return flag;
//		}
//		String[] tempList = file.list();
//		File temp = null;
//		for (int i = 0; i < tempList.length; i++) {
//			if (path.endsWith(File.separator)) {
//				temp = new File(path + tempList[i]);
//			} else {
//				temp = new File(path + File.separator + tempList[i]);
//			}
//			if (temp.isFile()) {
//				totalSize[0] += Tools.getFileSize(temp.toString());
//			}
//			if (temp.isDirectory()) {
//				getAllFileSize(path + "/" + tempList[i], totalSize);// 先删除文件夹里面的文件
//				flag = true;
//			}
//		}
//		return flag;
//	}
//
//	/**
//	 * 获取屏幕宽高
//	 *
//	 * @param context
//	 * @return int[]{screenWidth,screenHeight}
//	 */
//	public static int[] getScreenSize(Context context) {
//		DisplayMetrics dm = new DisplayMetrics();
//		WindowManager wm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
//		wm.getDefaultDisplay().getMetrics(dm);
//		int screenWidth, screenHeight;
//		screenWidth = dm.widthPixels;
//		screenHeight = dm.heightPixels;
//		return new int[]{screenWidth, screenHeight};
//	}
//
//	/**
//	 * 获取DisplayMetrics对象
//	 *
//	 * @param context
//	 * @return
//	 */
//	public static DisplayMetrics getDisplayMetrics(Context context) {
//		DisplayMetrics dm = new DisplayMetrics();
//		WindowManager wm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
//		wm.getDefaultDisplay().getMetrics(dm);
//		return dm;
//	}
//
//	public static void toast(Context context, String msg) {
//		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//	}
//
//	// 读取IMEI
//	public static String getIMEI(Context activity) {
//		TelephonyManager tm = (TelephonyManager) activity
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		String imei = tm.getDeviceId();
//		return imei;
//	}
//
//	// 检测网络是否可用
//	public static boolean hasNetwork(Context activity) {
//		ConnectivityManager cm = (ConnectivityManager) activity
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo ni = cm.getActiveNetworkInfo();
//		return ni != null && ni.isConnected();
//	}
//
//	// 判断是否wifi联网
//	public static boolean isWifi(Context activity) {
//		String type = getNetworkType(activity);
//		if (type.equals("NETTYPE_WIFI")) {
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 获取当前网络类型
//	 *
//	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
//	 */
//	public static String getNetworkType(Context activity) {
//		String netType = "none";
//		ConnectivityManager connectivityManager = (ConnectivityManager) activity
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//		if (networkInfo == null) {
//			return netType;
//		}
//		int nType = networkInfo.getType();
//		if (nType == ConnectivityManager.TYPE_MOBILE) {
//			String extraInfo = networkInfo.getExtraInfo();
//			if ((extraInfo != null) && (!extraInfo.equals(""))) {
//				if (extraInfo.toLowerCase().equals("cmnet")) {
//					netType = "NETTYPE_CMNET";
//				} else {
//					netType = "NETTYPE_CMWAP";
//				}
//			}
//		} else if (nType == ConnectivityManager.TYPE_WIFI) {
//			netType = "NETTYPE_WIFI";
//		}
//		return netType;
//	}
//
//	// 判断是不是合法的电话号码，只是简单判断了数字，+，-，空格
//	public static boolean isSimplePhone(String phoneNumber) {
//		boolean isValid = false;
//		String expression = "^[0-9+\\- ]+$";
//		CharSequence inputStr = phoneNumber;
//		Pattern pattern = Pattern.compile(expression);
//		Matcher matcher = pattern.matcher(inputStr);
//		if (matcher.matches()) {
//			isValid = true;
//		}
//		return isValid;
//	}
//
//	// 检查是不是手机号码
//	public static boolean isTelPhone(String paramString) {
//		return Pattern
//				.compile(
//						"^13[0-9]{1}[0-9]{8}$|14[0-9]{1}[0-9]{8}$|15[0-9]{1}[0-9]{8}$|16[0-9]{1}[0-9]{8}$|17[0-9]{1}[0-9]{8}$|18[0-9]{1}[0-9]{8}$")
//				.matcher(paramString).matches();
//	}
//
//	/**
//	 * @param telephone 输入的号码
//	 * @return 是否是座机或手机号码
//	 */
//	public static boolean isPhoneNumber(String telephone) {
//		if (!isTelPhone(telephone) && !isLandLine(telephone) && !is4or8HotLine(telephone)) {
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * @param phoneNumber 座机电话号码
//	 * @return 是否是座机号码
//	 */
//	public static boolean isLandLine(String phoneNumber) {
//		String regex = "^(\\d{3,4}|\\d{3,4}-)?\\d{7,8}$";
//		return Pattern.compile(regex).matcher(phoneNumber).matches();
//	}
//
//	/**
//	 * @param phoneNumber
//	 * @return 是否是400和800的电话
//	 */
//	public static boolean is4or8HotLine(String phoneNumber) {
//		String regex = "^[48]00-?\\d{4}-?\\d{3}$";
//		return Pattern.compile(regex).matcher(phoneNumber).matches();
//	}
//
//	// 检测是不是邮箱
//	public static boolean isEmail(String paramString) {
//		return Pattern
//				.compile(
//						"^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$")
//				.matcher(paramString).matches();
//	}
//
//	// 获取一个唯一的id
//	public static String unique(Context mContext) {
//		long time = new Date().getTime();
//		String id = Config.getUniqueUserId() + time + new Random(time).nextInt();
//		return id;
//	}
//
//	/**
//	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
//	 *
//	 * @param input
//	 * @return boolean
//	 */
//	public static boolean isEmpty(String input) {
//		if (input == null || "".equals(input))
//			return true;
//
//		for (int i = 0; i < input.length(); i++) {
//			char c = input.charAt(i);
//			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * 根据文件绝对路径获取文件名
//	 */
//	public static String getFileName(String filePath) {
//		if (isEmpty(filePath))
//			return "";
//		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
//	}
//
//	/**
//	 * 取SD卡路径，不带最后分隔符的; 如没有sd卡，返回data/data/files路径; 如有异常，返回空
//	 *
//	 * @return
//	 */
//	public static String getSdRootPath() {
//		File sdDir = null;
//		try {
//			boolean sdCardExist = Environment.getExternalStorageState().equals(
//					Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
//			if (sdCardExist) {
//				sdDir = Environment.getExternalStorageDirectory(); // 获取根目录
//			} else {
//				sdDir = SOSApplication.getAppContext().getFilesDir();
//			}
//			if (sdDir != null) {
//				return sdDir.getPath();
//			} else {
//				return "";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "";
//		}
//	}
//
//	// 获得项目在sd卡上的根目录, 不带结束符的
//	public static String getModDir() {
//		String strDir = getSdRootPath() + File.separator + "hecom";
//		File cacheDir = new File(strDir);
//		if (!cacheDir.exists()) {
//			cacheDir.mkdirs();
//		}
//		return strDir;
//	}
//
//	public static void main(String[] args) {
//		String str = "test";
//		System.out.println(isEmail(str));
//	}
//
//	/**
//	 * 发起自定义notification
//	 *
//	 * @param context
//	 * @param ticker         滚动的文字
//	 * @param contentTitle   文字标题
//	 * @param contentText    文字内容
//	 * @param pintent        点击意图
//	 * @param drawableId     展示图标
//	 * @param notificationID
//	 * @param soundUri       声音
//	 * @param priority       优先级
//	 */
//	public static void setCustomNotification(Context context, String ticker, String contentTitle,
//	                                         String contentText, PendingIntent pintent,
//	                                         int drawableId, int notificationID,
//	                                         Uri soundUri, int priority) {
//		if (context == null) {
//			return;
//		}
//		if (TextUtils.isEmpty(ticker)) {
//			ticker = "";
//		}
//		if (TextUtils.isEmpty(contentTitle)) {
//			contentTitle = "";
//		}
//		if (TextUtils.isEmpty(contentText)) {
//			contentText = "";
//		}
//		try {
//			NotificationManager mNotificationManager;
//			NotificationCompat.Builder notificationBuilder;
//			if (notificationID == -1) {
//				notificationID = 0x10000;
//			}
//			mNotificationManager = (NotificationManager) context
//					.getSystemService(Context.NOTIFICATION_SERVICE);
//			notificationBuilder = new NotificationCompat.Builder(context);
//			notificationBuilder.setSmallIcon(drawableId);
//			notificationBuilder.setTicker(ticker);
//			notificationBuilder.setContentTitle(contentTitle);
//			notificationBuilder.setContentText(contentText);
//			notificationBuilder.setAutoCancel(true);
//			notificationBuilder.setSound(soundUri);
//			notificationBuilder.setPriority(priority);
//			if (pintent != null) {
//				notificationBuilder.setContentIntent(pintent);
//			}
//			mNotificationManager.notify(notificationID, notificationBuilder.build());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 发起notification
//	 *
//	 * @param context
//	 * @param ticker
//	 * @param contentTitle
//	 * @param contentText
//	 * @param pintent      如果点击没有动作，可以设为null
//	 * @param drawableId   R.drawabe.*
//	 */
//	public static void setNotification(Context context, String ticker, String contentTitle,
//	                                   String contentText, PendingIntent pintent, int drawableId, int notificationID) {
//		if (context == null) {
//			return;
//		}
//		if (TextUtils.isEmpty(ticker)) {
//			ticker = "测试";
//		}
//		if (TextUtils.isEmpty(contentTitle)) {
//			contentTitle = "测试";
//		}
//		if (TextUtils.isEmpty(contentText)) {
//			contentText = "测试";
//		}
//		try {
//			NotificationManager mNotificationManager;
//			NotificationCompat.Builder notificationBuilder;
//			if (notificationID == -1) {
//				notificationID = 0x10000;
//			}
//			mNotificationManager = (NotificationManager) context
//					.getSystemService(Context.NOTIFICATION_SERVICE);
//			notificationBuilder = new NotificationCompat.Builder(context);
//			notificationBuilder.setSmallIcon(drawableId);
//			notificationBuilder.setTicker(ticker);
//			notificationBuilder.setContentTitle(contentTitle);
//			notificationBuilder.setContentText(contentText);
//			notificationBuilder.setAutoCancel(true);
//			notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
//			// notificationBuilder.setNumber(1);
//			if (pintent != null) {
//				notificationBuilder.setContentIntent(pintent);
//			}
//			mNotificationManager.notify(notificationID, notificationBuilder.build());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static final String BASE_URL = "http://gg.hecom.cn/h5mobile/app/index.php";
//
//	private static String getShareURL(){
//		UserInfo info=UserInfo.getUserInfo();
//		return BASE_URL + "?uid=" + info.getUid() + "&entCode=" + info.getUserEntCode() + "&name=" + info.getName() + "&entName=" + info.getEntName();
//	}
//
//	/**
//	 * 社交化分享
//	 *
//	 * @param mContext
//	 * @param title       公共title,默认为“红圈管理，最懂销售”,可以为空
//	 * @param titileUrl   公共title的超链接,预先设定了产品web地址,可以为空
//	 * @param contentText 公共contentText
//	 * @param contentUrl  公共contentText的超链接,预先设定了产品web地址,可以为空
//	 * @param imgUrl      公共image,默认为http://cip.sosgps.com.cn/androidapp/logo.png
//	 * @param comment     预先设定的评论内容,可以为空
//	 * @param siteName    预先设定的名字,可以为空
//	 * @param siteUrl     siteUrl是分享此内容的网站地址，仅在QQ空间使用
//	 */
//	public static void showShare(Context mContext, String title, String titileUrl,
//	                             String contentText, String contentUrl, String imgUrl, String comment, String siteName,
//	                             String siteUrl) {
//		final Context context = mContext.getApplicationContext();
//		ShareSDK.initSDK(context);
//		OnekeyShare oks = new OnekeyShare();
//		// 关闭sso授权
//		oks.disableSSOWhenAuthorize();
//		oks.setSilent(true);
//		if (TextUtils.isEmpty(title)) {
//			title = "红圈管理，最懂销售";
//		}
//		if (TextUtils.isEmpty(titileUrl)) {
//			titileUrl = getShareURL();
//		}
//		if (TextUtils.isEmpty(contentUrl)) {
//			contentUrl = getShareURL();
//		}
//		if (TextUtils.isEmpty(contentText)) {
//			contentText = "专注解决您在销售管理过程中最头痛的问题。外勤拜访真实有效，工作执行标准精确，实时信息动态决策，企业微信高效沟通。——红圈管理，中国排名第一的企业级移动销售云服务";
//		}
//		String shareContentText = contentText + contentUrl;
//		if (TextUtils.isEmpty(imgUrl)) {
//			imgUrl = "http://gg.hecom.cn/h5mobile/app/img/logo60.png";
//		}
//		if (TextUtils.isEmpty(comment)) {
//			comment = "红圈管理，中国排名第一的企业级移动销售云服务";
//		}
//		if (TextUtils.isEmpty(siteName)) {
//			siteName = context.getString(R.string.app_name);
//		}
//		if (TextUtils.isEmpty(siteUrl)) {
//			siteUrl = getShareURL();
//		}
//		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//		oks.setTitle(title);
//		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		oks.setTitleUrl(titileUrl);
//		// text是分享文本，所有平台都需要这个字段
//		oks.setText(shareContentText);
//		// url仅在微信（包括好友和朋友圈）中使用
//		oks.setUrl(contentUrl);
//		// 图片url
//		oks.setImageUrl(imgUrl);
//		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		oks.setComment(comment);
//		// site是分享此内容的网站名称，仅在QQ空间使用
//		oks.setSite(siteName);
//		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
//		oks.setSiteUrl(siteUrl);
//
//		// remove comments, shows how to add custom logos in platform gridview
//		Bitmap enableLogo = BitmapFactory.decodeResource(context.getResources(),
//				R.drawable.share_more);
//		Bitmap disableLogo = BitmapFactory.decodeResource(context.getResources(),
//				R.drawable.share_more);
//		String label = "更多";
//		final String innerContentString = contentText;
//		final String innerContentUrl = contentUrl;
//		OnClickListener listener = new OnClickListener() {
//			public void onClick(View v) {
//				oneKeyShare(context, innerContentString, innerContentUrl);
//			}
//		};
//		oks.setCustomerLogo(enableLogo, disableLogo, label, listener);
//
//		// 启动分享GUI
//		oks.show(context);
//	}
//
//	/**
//	 * 社交化分享备用方法
//	 *
//	 * @param mContext
//	 * @param title       公共title,默认为“红圈管理，最懂销售”,可以为空
//	 * @param titileUrl   公共title的超链接,预先设定了产品web地址,可以为空
//	 * @param contentText 公共contentText
//	 * @param contentUrl  公共contentText的超链接,预先设定了产品web地址,可以为空
//	 * @param imgUrl      公共image,默认为http://cip.sosgps.com.cn/androidapp/logo.png
//	 * @param comment     预先设定的评论内容,可以为空
//	 * @param siteName    预先设定的名字,可以为空
//	 * @param siteUrl     siteUrl是分享此内容的网站地址，仅在QQ空间使用
//	 */
//	public static void showShare2(Context mContext, String title, String titileUrl,
//	                              String contentText, String contentUrl, String imgUrl, String comment, String siteName,
//	                              String siteUrl) {
//		final Context context = mContext.getApplicationContext();
//		ShareSDK.initSDK(context);
//		OnekeyShare oks = new OnekeyShare();
//
//		String webSiteUrl = getShareURL();
//		String commonLogUrl = "http://gg.hecom.cn/h5mobile/app/img/logo60.png";
//		String defalutContent = "专注解决您在销售管理过程中最头痛的问题。外勤拜访真实有效，工作执行标准精确，实时信息动态决策，企业微信高效沟通。——红圈管理，中国排名第一的企业级移动销售云服务";
//		if (TextUtils.isEmpty(title)) {
//			title = "红圈管理，最懂销售";
//		}
//		if (TextUtils.isEmpty(titileUrl)) {
//			titileUrl = webSiteUrl;
//		}
//		if (TextUtils.isEmpty(contentUrl)) {
//			contentUrl = webSiteUrl;
//		}
//		if (TextUtils.isEmpty(contentText)) {
//			contentText = defalutContent;
//		}
//		String shareContentText = contentText + contentUrl;
//		if (TextUtils.isEmpty(imgUrl)) {
//			imgUrl = commonLogUrl;
//		}
//		if (TextUtils.isEmpty(siteName)) {
//			siteName = context.getString(R.string.app_name);
//		}
//		if (TextUtils.isEmpty(siteUrl)) {
//			siteUrl = webSiteUrl;
//		}
//		oks.setTitle(title);
//		oks.setTitleUrl(titileUrl);
//		// contentText =
//		// "ShareSDK不仅集成简单、支持如QQ好友、微信、新浪微博、腾讯微博等所有社交平台，而且还有强大的统计分析管理后台，实时了解用户、信息流、回流率、传播效应等数据，详情见官网http://mob.com/ @Mob移动开发者服务平台";
//		oks.setText(contentText);
//
//		oks.setImageUrl(commonLogUrl);
//		// oks.setImagePath("/storage/emulated/0/ShareSDK/cn.sharesdk.demo/cache/pic_lovely_cats.jpg");
//		oks.setUrl(contentUrl);
//		// oks.setFilePath("/storage/emulated/0/ShareSDK/cn.sharesdk.demo/cache/pic_lovely_cats.jpg");
//		oks.setComment(shareContentText);
//		oks.setSite(siteName);
//		oks.setSiteUrl(siteUrl);
//
//		oks.setSilent(false);
//
//		// oks.setTheme(OnekeyShareTheme.SKYBLUE);
//		oks.setTheme(OnekeyShareTheme.CLASSIC);
//
//		// 令编辑页面显示为Dialog模式
//		oks.setDialogMode();
//
//		// 在自动授权时可以禁用SSO方式
//		// if(!CustomShareFieldsPage.getBoolean("enableSSO", true))
//		oks.disableSSOWhenAuthorize();
//
//		// 去除注释，演示在九宫格设置自定义的图标
//		Bitmap enableLogo = BitmapFactory
//				.decodeResource(context.getResources(), R.drawable.logo_qq);
//		Bitmap disableLogo = BitmapFactory.decodeResource(context.getResources(),
//				R.drawable.logo_qq);
//		String label = context.getResources().getString(R.string.app_name);
//		OnClickListener listener = new OnClickListener() {
//			public void onClick(View v) {
//				String text = "Customer Logo -- ShareSDK ";
//				// Toast.makeText(getContext(), text,
//				// Toast.LENGTH_SHORT).show();
//			}
//		};
//		oks.setCustomerLogo(enableLogo, disableLogo, label, listener);
//		oks.setCustomerLogo(enableLogo, disableLogo, label, listener);
//		oks.setCustomerLogo(enableLogo, disableLogo, label, listener);
//
//		// 启动分享GUI
//		oks.show(context);
//	}
//
//	/**
//	 * 分享其他
//	 *
//	 * @param context
//	 */
//	private static void oneKeyShare(Context context, String contentText, String url) {
//		context = context.getApplicationContext();
//		if (TextUtils.isEmpty(contentText)) {
//			contentText = "专注解决您在销售管理过程中最头痛的问题。外勤拜访真实有效，工作执行标准精确，实时信息动态决策，企业微信高效沟通。——红圈管理，中国排名第一的企业级移动销售云服务 ";
//		}
//
//		if (TextUtils.isEmpty(url)) {
//			url = getShareURL();
//		}
//		contentText = contentText + url;
//		Intent sendIntent = new Intent();
//		sendIntent.setAction(Intent.ACTION_SEND);
//		sendIntent.putExtra(Intent.EXTRA_TEXT, contentText);
//		sendIntent.setType("text/plain");
//		context.startActivity(Intent.createChooser(sendIntent, "分享到").addFlags(
//				Intent.FLAG_ACTIVITY_NEW_TASK));
//	}
//
//	/**
//	 * 判断字符串是否为数字
//	 *
//	 * @param str
//	 * @return
//	 */
//	public static boolean isNumeric(String str) {
//		if (str == null || str.equals("")) {
//			return false;
//		}
//		Pattern pattern = Pattern.compile("[0-9]*");
//		Matcher isNum = pattern.matcher(str);
//		if (!isNum.matches()) {
//			return false;
//		}
//		return true;
//	}
//

    /**
     * 获取程序包名
     *
     * @param cx
     * @return 程序包名
     */
    public static String getPackageName(Context cx) {
        String packageName = ""; // 初始化
        PackageManager packageManager = cx.getApplicationContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(cx.getPackageName(), 0);
            packageName = packageInfo.packageName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        return packageName;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info == null) {
                return false;
            } else {
                if (info.isAvailable()) {
                    return true;
                }

            }
        }
        return false;
    }


    /**
     * 判断APK是否注册某项权限
     *
     * @param cx
     * @param permissionName 权限名称
     * @return
     */
    public static boolean checkPermission(Context cx, String permissionName) {
        try {
            PackageManager pm = cx.getPackageManager();
            int res = pm.checkPermission(permissionName, getPackageName(cx));
            if (res == PackageManager.PERMISSION_DENIED) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
//
//	/**
//	 * 判断当前app是否正在显示（最前端）
//	 *
//	 * @param mContext
//	 * @return
//	 */
//	public static boolean currentAppIsForeGround(Context mContext) {
//		try {
//			ActivityManager am = (ActivityManager) mContext
//					.getSystemService(Context.ACTIVITY_SERVICE);
//			String curPackageName = mContext.getPackageName();
//			List<RunningTaskInfo> list = am.getRunningTasks(1);
//			if (!list.isEmpty()) {
//				ComponentName topActivity = list.get(0).topActivity;
//				String packageName = topActivity.getPackageName();
//				if (packageName.equals(curPackageName)) {
//					return true;
//				}
//			}
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//

    /**
     * 获得保存图片的路径+文件名
     * <p>
     * 根据不用业务，使用pictmp/folderType/指定的照片存储路径。如果为空，则使用默认的pictmp/路径
     * </p>
     * 建议使用folderType。例如在启动CamerActivity的时候，传入"typeFileFolder"参数,来制定不同业务,
     * 使用不同的图片文件夹
     *
     * @param account    账号
     * @param folderType 不同的业务文件夹路径
     * @return
     */
    public static String getPicSaveFilePath(String account, String folderType) {
        /* 照片将被保存到 SD 卡跟目录下，文件名为系统时间，后缀名为".jpg" */
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileDir = getPicSaveDir(folderType);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fileDir);
        stringBuilder.append(account);
        stringBuilder.append("_");
        stringBuilder.append(formatter.format(new Date()));
        stringBuilder.append(".jpg");
        return stringBuilder.toString();
    }

//
//	//本地图片按照服务器规则命名后,获取取此图片在服务器保存的URL
//	public static String getRemoteUrl(String localFilePath) {
//		if (TextUtils.isEmpty(localFilePath)) {
//			return localFilePath;
//		}
//
//		String separator = "/";
//		int seperator_index = localFilePath.lastIndexOf(separator);
//		if (seperator_index >= 0) {
//			if (seperator_index == localFilePath.length() - 1) {
//				throw new IllegalArgumentException("localFilePath不能以/结束");
//			}
//			localFilePath = localFilePath.substring(seperator_index + 1);
//		}
//
//		int dotIndex = localFilePath.lastIndexOf(".");
//		String fileExtName = "";
//		String fileBaseName = localFilePath;
//		if (dotIndex >= 0) {
//			fileExtName = localFilePath.substring(dotIndex + 1);
//			fileBaseName = localFilePath.substring(0, dotIndex);
//		}
//
//		UserInfo userInfo = UserInfo.getUserInfo();
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
//
//		fileExtName = fileExtName == null ? "" : "." + fileExtName;
//		HLog.v("Tools", "fileBaseName:" + fileBaseName + ",fileExtName:"
//				+ fileExtName);
//		String year;
//		String month;
//
//		Boolean isUsePhotoTimeAsFilePath = true;
//		if (isUsePhotoTimeAsFilePath) {
//			String[] nameArray = fileBaseName.split("_");
//			String sendDate = nameArray[1];
//			year = sendDate.substring(0, 4);
//			month = sendDate.substring(4, 6);
//			if (month.startsWith("0")) {
//				month = month.substring(1);
//			}
//		} else {
//			year = calendar.get(Calendar.YEAR) + "";
//			month = (calendar.get(Calendar.MONTH) + 1) + "";
//		}
//
//		return new StringBuilder(Config.getHost())
//				.append("filemanage").append(separator)
//				.append("photoFiles").append(separator)
//				.append(year).append(separator)       //年
//				.append(month).append(separator)      //月
//				.append(userInfo.getEntCode()).append(separator)    //企业代码
//				.append(userInfo.getUid()).append(separator)   //终端ID
//				.append(fileBaseName).append(fileExtName)
//				.toString();
//	}
//
//	/**
//	 * 获得保存图片的文件夹路径
//	 * <p>
//	 * 根据不用业务，使用pictmp/folderType/指定的照片存储路径。如果为空，则使用默认的pictmp/路径
//	 * </p>
//	 * 建议在启动CamerActivity的时候，传入"typeFileFolder"参数,来制定不同业务，使用不同的图片文件夹
//	 *
//	 * @param folderType 不同的业务文件夹路径
//	 * @return
//	 */
//	public static String getPicSaveDir(String folderType) {
//		StringBuilder stringBuilder = new StringBuilder();
//		stringBuilder.append(Tools.getSdRootPath());
//		stringBuilder.append("/");
//		stringBuilder.append(Config.FILE_PIC_TMP_DIR);
//		if (!TextUtils.isEmpty(folderType)) {
//			stringBuilder.append(folderType);
//			stringBuilder.append("/");
//		}
//		String fileDir = stringBuilder.toString();
//		File filePth = new File(fileDir);
//		if (!filePth.exists()) {
//			filePth.mkdirs();
//		}
//		return fileDir;
//	}
//
//	/**
//	 * 压缩图片（质量）大小
//	 *
//	 * @param image
//	 * @param Factor 比例缩放倍数
//	 * @return
//	 */
//	public static Bitmap compressImage(Bitmap image, int Factor) {
//		ByteArrayOutputStream output = null;
//		ByteArrayInputStream input = null;
//		int options = 100;
//		try {
//			output = new ByteArrayOutputStream();
//			image.compress(CompressFormat.JPEG, 100, output);
//			while (output.toByteArray().length / 1024 > 1024) { // 如果大于一兆，压缩
//				output.reset();
//				if (options > 0) {
//					options = options - 20;
//				} else {
//					break;
//				}
//				image.compress(CompressFormat.JPEG, options, output);
//			}
//			BitmapFactory.Options newOpts = new BitmapFactory.Options();
//			input = new ByteArrayInputStream(output.toByteArray());
//			newOpts.inJustDecodeBounds = true;
//			image = BitmapFactory.decodeStream(input, null, newOpts);
//			newOpts.inJustDecodeBounds = false;
//			newOpts.inSampleSize = Factor;// 设置缩放比例
//			newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
//			input = new ByteArrayInputStream(output.toByteArray());
//			image = BitmapFactory.decodeStream(input, null, newOpts);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (output != null) {
//				try {
//					output.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				output = null;
//			}
//			if (input != null) {
//				try {
//					input.close();
//					input = null;
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return image;
//	}
//
//	/**
//	 * 获取当前时间long值
//	 *
//	 * @return
//	 */
//	public static long getCurrentLongTime() {
//		Calendar calendar = Calendar.getInstance();
//		return calendar.getTimeInMillis();
//	}
//
//	/**
//	 * 获取今天 昨天 几月几号
//	 *
//	 * @param time
//	 * @return
//	 */
//	public static String getActiveDay(long time) {
//		Calendar nowCalendar = Calendar.getInstance(Locale.CHINA);
//		Calendar calendar = Calendar.getInstance(Locale.CHINA);
//		calendar.setTimeInMillis(time);
//		long todayMills = getTodayMills();
//		long oneDayMills = getOneDayMills();
//		long timeMills = calendar.getTime().getTime();
//		if (timeMills >= todayMills) {
//			if (timeMills >= todayMills + oneDayMills) {
//				return calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
//						+ calendar.get(Calendar.DAY_OF_MONTH) + "日";
//			} else {
//				return "今天";
//			}
//		} else if (timeMills < todayMills && timeMills >= todayMills - oneDayMills) {
//			return "昨天";
//		} else if (nowCalendar.get(Calendar.YEAR) != calendar.get(Calendar.YEAR)) {
//			return calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
//					+ calendar.get(Calendar.DAY_OF_MONTH) + "日";
//		} else {
//			return (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH)
//					+ "日";
//		}
//	}
//
//	/**
//	 * 获取今天0点的毫秒数
//	 *
//	 * @return
//	 */
//	public static long getTodayMills() {
//		Calendar calendar = Calendar.getInstance(Locale.CHINA);
//		calendar.set(Calendar.HOUR_OF_DAY, 0);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//		calendar.set(Calendar.MILLISECOND, 0);
//		return calendar.getTime().getTime();
//	}
//
//	/**
//	 * 获取今天24点的毫米数
//	 *
//	 * @return
//	 */
//	public static long getTodayMillsEnd() {
//		Calendar calendar = Calendar.getInstance(Locale.CHINA);
//		calendar.set(Calendar.HOUR_OF_DAY, 24);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//		calendar.set(Calendar.MILLISECOND, 0);
//		return calendar.getTime().getTime();
//	}
//
//
//	/**
//	 * 本周周一0点的毫米数
//	 *
//	 * @return
//	 */
//	public static long getWeekMills() {
//
//		Calendar cal = Calendar.getInstance();
//		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
//		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//		return cal.getTime().getTime();
//	}
//
//	/**
//	 * 本周日24点毫秒数
//	 */
//
//	public static long getWeekMillsEnd() {
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(new Date(getWeekMills()));
//		cal.add(Calendar.DAY_OF_WEEK, 7);
//		return cal.getTime().getTime();
//	}
//
//	/**
//	 * 本月第一天0点毫秒数
//	 */
//	public static long getMonthMills() {
//		Calendar cal = Calendar.getInstance();
//		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
//		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
//		return cal.getTime().getTime();
//	}
//
//	/**
//	 * 本月最后一天毫秒数
//	 */
//
//	public static long getMonthMillsEnd() {
//		Calendar cal = Calendar.getInstance();
//		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
//		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
//		cal.set(Calendar.HOUR_OF_DAY, 24);
//		return cal.getTime().getTime();
//	}
//
//	/**
//	 * 上月第一天0点毫秒数
//	 */
//	public static long getLastMonthMills() {
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(new Date(getMonthMills()));
//		cal.add(Calendar.MONTH, -1);
//		return cal.getTime().getTime();
//	}
//
//	/**
//	 * 获取给定日期的开始毫秒数
//	 *
//	 * @param date
//	 * @return
//	 */
//	public static long getDayStart(Date date) {
//		Calendar calendar = Calendar.getInstance(Locale.CHINA);
//		calendar.setTime(date);
//		calendar.set(Calendar.HOUR_OF_DAY, 0);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//		calendar.set(Calendar.MILLISECOND, 0);
//		return calendar.getTime().getTime();
//	}
//
//	/**
//	 * 获取给定日期的结束毫秒数
//	 *
//	 * @param date
//	 * @return
//	 */
//	public static long getDayEnd(Date date) {
//		Calendar calendar = Calendar.getInstance(Locale.CHINA);
//		calendar.setTime(date);
//		calendar.set(Calendar.HOUR_OF_DAY, 24);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//		calendar.set(Calendar.MILLISECOND, 0);
//		return calendar.getTime().getTime();
//	}
//
//
//	/**
//	 * 获取给定日期的开始毫秒数
//	 *
//	 * @param dateStr
//	 * @param format
//	 * @return
//	 */
//	public static long getDayStart(String dateStr, String format) {
//		SimpleDateFormat sdf = new SimpleDateFormat(format);
//		try {
//			return getDayStart(sdf.parse(dateStr));
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}
//
//	/**
//	 * 获取给定日期的结束毫秒数
//	 *
//	 * @param dateStr
//	 * @param format
//	 * @return
//	 */
//	public static long getDayEnd(String dateStr, String format) {
//		SimpleDateFormat sdf = new SimpleDateFormat(format);
//		try {
//			return getDayEnd(sdf.parse(dateStr));
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}
//
//	/**
//	 * 1天的毫秒数
//	 *
//	 * @return
//	 */
//	public static long getOneDayMills() {
//		return 24 * 60 * 60 * 1000;
//	}
//
//	/**
//	 * 获取几点几分
//	 *
//	 * @param time
//	 * @return
//	 */
//	public static String getHourAndMinute(long time) {
//		Calendar calendar = Calendar.getInstance(Locale.CHINA);
//		calendar.setTimeInMillis(time);
//		return getAddZero(calendar.get(Calendar.HOUR_OF_DAY)) + ":"
//				+ getAddZero(calendar.get(Calendar.MINUTE));
//	}
//
//	/**
//	 * @param time
//	 * @return
//	 */
//	public static String getTimeStr(Long time) {
//		String strTime = "";
//		Long currentTime = System.currentTimeMillis();
//		long minute = DateTool.differMinute(time, currentTime);
//		if (minute < 60) {// 显示分钟
//			if (minute == 0) {
//				strTime = "刚刚";
//			}
//			strTime = String.valueOf(minute) + "分钟前";
//		} else if (minute >= 60 && minute < 1440) {// 显示小时
//			strTime = String.valueOf(DateTool.differHours(time, currentTime)) + "小时前";
//		} else if (minute >= 1440) {// 显示天
//			Calendar calendar = Calendar.getInstance(Locale.CHINA);
//			calendar.setTimeInMillis(time);
//			return getAddZero(calendar.get(Calendar.HOUR_OF_DAY)) + ":"
//					+ getAddZero(calendar.get(Calendar.MINUTE));
//		}
//
//		return strTime;
//	}
//
//	/**
//	 * 获取时间的字符串形式,不是本年显示年份,不是本日显示日期
//	 *
//	 * @param time
//	 * @return
//	 */
//	public static String getActiveTime(long time) {
//		Calendar nowCalendar = Calendar.getInstance(Locale.CHINA);
//		Calendar calendar = Calendar.getInstance(Locale.CHINA);
//		calendar.setTimeInMillis(time);
//		if (calendar.get(Calendar.YEAR) != nowCalendar.get(Calendar.YEAR)) {
//			return calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
//					+ calendar.get(Calendar.DAY_OF_MONTH) + "日    "
//					+ getAddZero(calendar.get(Calendar.HOUR_OF_DAY)) + ":"
//					+ getAddZero(calendar.get(Calendar.MINUTE));
//		} else if (calendar.get(Calendar.DAY_OF_YEAR) == nowCalendar.get(Calendar.DAY_OF_YEAR)) {
//			return getAddZero(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + getAddZero(
//					calendar.get(Calendar.MINUTE));
//		} else {
//			if (nowCalendar.get(Calendar.YEAR) != calendar.get(Calendar.YEAR)) {
//				return calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
//						+ calendar.get(Calendar.DAY_OF_MONTH) + "日  "
//						+ getAddZero(calendar.get(Calendar.HOUR_OF_DAY)) + ":"
//						+ getAddZero(calendar.get(Calendar.MINUTE));
//			} else {
//				return (calendar.get(Calendar.MONTH) + 1) + "月"
//						+ calendar.get(Calendar.DAY_OF_MONTH) + "日  "
//						+ getAddZero(calendar.get(Calendar.HOUR_OF_DAY)) + ":"
//						+ getAddZero(calendar.get(Calendar.MINUTE));
//			}
//		}
//	}
//
//	/**
//	 * 获取时间的字符串形式,必达使用
//	 *
//	 * @param time
//	 * @return
//	 */
//	public static String getDuangActiveTime(long time) {
//		Calendar nowCalendar = Calendar.getInstance(Locale.CHINA);
//		Calendar calendar = Calendar.getInstance(Locale.CHINA);
//		calendar.setTimeInMillis(time);
//		if (calendar.get(Calendar.YEAR) != nowCalendar.get(Calendar.YEAR)) {
//			return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-"
//					+ calendar.get(Calendar.DAY_OF_MONTH) + " "
//					+ getAddZero(calendar.get(Calendar.HOUR_OF_DAY)) + ":"
//					+ getAddZero(calendar.get(Calendar.MINUTE));
//		}else {
//			return (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日 "
//				+ getAddZero(calendar.get(Calendar.HOUR_OF_DAY)) + ":"+ getAddZero(calendar.get(Calendar.MINUTE));
//		}
//	}
//
//	/**
//	 * 一位时前面补0
//	 *
//	 * @param i
//	 * @return
//	 */
//	public static String getAddZero(int i) {
//		if (i < 10) {
//			return "0" + i;
//		} else {
//			return Integer.toString(i);
//		}
//	}
//
//	/**
//	 * 获得自己的employeeCode
//	 */
//	public static String getEmployeeCode() {
//		UserInfo userInfo = UserInfo.getUserInfo();
//		String myEmpCode = userInfo.getEmpCode();
//		return myEmpCode;
//	}
//
//	/**
//	 * yyyy-MM 转 yy-MM
//	 *
//	 * @param s
//	 * @return
//	 */
//	public static String getAbridgeDate(String s) {
//		return s.substring(s.length() - 5);
//	}
//
//	private DismissCallBack dismissCallBack;
//
//	public interface DismissCallBack {
//		public void onDismiss();
//	}
//
//	public void setDismissCallBack(DismissCallBack dismissCallBack) {
//		this.dismissCallBack = dismissCallBack;
//	}
//
//	public DismissCallBack getDismissCallBack() {
//		return dismissCallBack;
//	}
//
//	/**
//	 * 演示版显示购买正式版的提示
//	 */
//	public static void showBuyFormalVersionDialog(final Activity context, String text, final BaseMainFragment fragment) {
//
//		final Dialog dialog = new Dialog(context, R.style.DialogNoTitle);
//		dialog.setContentView(R.layout.buy_formal_version_dialog);
//		dialog.getWindow().findViewById(R.id.btn_later).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//			}
//		});
//
//		dialog.getWindow().findViewById(R.id.btn_buy_formal_version)
//				.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Intent purchaseIntent = new Intent(context,
//								OrderConfirmActivity.class);
//						context.startActivity(purchaseIntent);
//						dialog.dismiss();
//					}
//				});
//		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//			                            public void onDismiss(DialogInterface dialog) {
//				                            if (fragment != null) {
//					                            fragment.showTipDialog();
//					                            CustomerGuideHelper.setGuidShow(CustomerGuideHelper.GUIDE_DIALOG_FROM_IS_EXP, true);
//				                            }
//			                            }
//		                            }
//
//		);
//
//		dialog.setCancelable(false);
//		dialog.setCanceledOnTouchOutside(false);
//		TextView tv = (TextView) dialog.getWindow().findViewById(R.id.text_content);
//		tv.setText(text);
//		dialog.show();
//
//	}
//
//
//	/**
//	 * @return 得到审批时自己的名字
//	 */
//	public static String getApplySelfName() {
//		UserInfo userInfo = UserInfo.getUserInfo();
//		if (userInfo == null) {
//			return "";
//		}
//		return TextUtils.isEmpty(userInfo.getName()) ? "" : userInfo.getName();
//	}
//
//	/**
//	 * @param employee_code
//	 * @param flowEmpInfo
//	 * @return 从审批流得到名字，这样显示审批流程时即使该用户被删除也能得到删除的
//	 */
//	public static String getEmployNameFromEmpInfo(String employee_code, String flowEmpInfo) {
//		try {
//			if (TextUtils.isEmpty(flowEmpInfo)) {
//				Map<String, IMFriend> friendMap = SOSApplication.getInstance().getUserCodeFriendmap();
//				if (friendMap != null) {
//					IMFriend imFriend = friendMap.get(employee_code);
//					if (imFriend != null) {
//						return imFriend.getName();
//					} else {
//						return "";
//					}
//				}
//				return "";
//			}
//			JSONArray jsonArray = new JSONArray(flowEmpInfo);
//			if (jsonArray != null && jsonArray.length() != 0) {
//				for (int i = 0; i < jsonArray.length(); i++) {
//					JSONObject jsonObject = jsonArray.getJSONObject(i);
//					if (jsonObject.has("code")) {
//						String jsonCode = jsonObject.get("code").toString();
//						if (jsonCode.equals(employee_code)) {
//							if (jsonObject.has("name")) {
//								return jsonObject.get("name").toString();
//							}
//						}
//					}
//				}
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
//
//	/**
//	 * 是否开启定位
//	 *
//	 * @param context
//	 * @return
//	 */
//	public static boolean isLocationEnabled(Context context) {
//		LocationManager locationManager = (LocationManager) context.
//				getSystemService(Context.LOCATION_SERVICE);
//		try {
//			return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//		} catch (Exception e) {
//            CrashReport.putUserData(SOSApplication.getInstance(), "crashPosition", "是否开启定位");
//			CrashReport.postCatchedException(e);
//		}
//		return false;
//	}
//
//	/**
//	 * 获取应用名称
//	 *
//	 * @param context
//	 * @return
//	 */
//	public static String getAppName() {
//		PackageManager packageManager = SOSApplication.getAppContext().getPackageManager();
//		ApplicationInfo applicationInfo = SOSApplication.getAppContext().getApplicationInfo();
//		return applicationInfo.loadLabel(packageManager).toString();
//	}
//
//	/**
//	 * 隐藏软键盘
//	 */
//	public static void hideSoftInput(IBinder windowToken) {
//		InputMethodManager imm = (InputMethodManager) SOSApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(windowToken, 0);
//	}
//	/**
//	 * 显示软键盘
//	 * imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//	 * boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
//	 */
//	public static void showSoftInput(View view) {
//		InputMethodManager imm = (InputMethodManager) SOSApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
//	}
//
//	public static String getRandomString(int length) { //length表示生成字符串的长度
//		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
//		Random random = new Random();
//		StringBuffer sb = new StringBuffer();
//		for (int i = 0; i < length; i++) {
//			int number = random.nextInt(base.length());
//			sb.append(base.charAt(number));
//		}
//		return sb.toString();
//	}


    public static void downloadFile(String fileURL, String saveDir)
            throws Exception {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
            scanFile(OMSClientApplication.getInstance(), saveFilePath);
            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }

    /**
     * 通知媒体库更新文件
     *
     * @param context
     * @param filePath 文件全路径
     */
    public static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

}
