package com.sosgps.location.service;

import java.util.Date;

import com.current.utils.DateTool;
import com.hecom.log.HLog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

/**
 * 常驻后台定时服务基类
 * 
 */
public abstract class TimedService extends Service {

	private static final String TAG = "TimedService";
	private static final long DEFAULT_TIME_INTERVAL = 10;//s
	public static final int STOP_COMMAND = 1;
	public static final String COMMAND_KEY = "COMMAND";

	// 只处理intent
	private Looper mLooper;
	private TaskHandler mTaskHandler;
	private boolean mRedelivery;
	private boolean mIsDeamonService;
	private String mActionName;
	
	private boolean isDaemonService() {
		return mIsDeamonService;
	}
	
	private static class TaskHandler extends Handler {
		private TimedService mService;
		
        public TaskHandler(Looper looper, TimedService service) {
            super(looper);
            mService = service;
        }

        @Override
        public void handleMessage(Message msg) {
        	mService.execute((Intent)msg.obj);
            if(!mService.isDaemonService()) {
            	mService.stopSelf(msg.arg1);
            }
        }
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/**
	 * 设置执行条件
	 * @return
	 */
	public abstract boolean needDoWork();
	
	/**
	 * 获取执行时间间隔
	 * @return
	 */
	public long getTimeInterval() {
		return DEFAULT_TIME_INTERVAL;
	}
	
	/**
	 * 设置是否为常驻服务
	 * @param isDeamon
	 */
	public void setDeamonService(boolean isDeamon) {
		mIsDeamonService = isDeamon;
	}
	
	/**
	 * 设置重启后是否重发上一条未执行完的命令
	 * @param enabled
	 */
    public void setIntentRedelivery(boolean enabled) {
        mRedelivery = enabled;
    }
	
	public String getActionName() {
		return mActionName;
	}
	
	/**
	 * 命令执行体, 子类实现具体的处理
	 * @param intent
	 */
	public void execute(Intent intent) {
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		HLog.i(TAG, "TimedService onCreate");
		mActionName = mActionName == null ? this.getClass().getName() : mActionName;
	}
	
	private void initThread() {
		HandlerThread thread = new HandlerThread(mActionName);
		thread.start();
		mLooper = thread.getLooper();
		mTaskHandler = new TaskHandler(mLooper, this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		int command = intent == null ? 0 : intent.getIntExtra(COMMAND_KEY, 0);
		if(command == STOP_COMMAND) {
			stopTimedService();
			return START_NOT_STICKY;
		}
		if(mLooper == null) {
			initThread();
		}
		long timeInterval = getTimeInterval() * 1000;
		HLog.i(TAG, "myprocess: " + Process.myPid());
		if (needDoWork()) {
			HLog.i(TAG, "onStartCommand to do work at: " + DateTool.dateToString(
					new Date(), DateTool.D_T_FORMAT));
			SharedPreferences sharedPrefrences = this.getSharedPreferences("TimedService",
					Activity.MODE_PRIVATE);
			String lastTimePrefName = getActionName() + "_lastRequestTime";
			long lastHandleTimeMillis = sharedPrefrences.getLong(lastTimePrefName, 0);
			long now = System.currentTimeMillis();
			long timeFromLastHandle = now - lastHandleTimeMillis;
			//距离上次请求的时间需接近设定的请求间隔时间
			if(timeFromLastHandle >= timeInterval) {
				Message msg = mTaskHandler.obtainMessage();
		        msg.arg1 = startId;
		        msg.obj = intent;
		        mTaskHandler.sendMessage(msg);
				//设定下一次启动时间
				startTimedService(timeInterval);
				//记录请求时间
				SharedPreferences.Editor editor = sharedPrefrences.edit();
				editor.putLong(lastTimePrefName, now);
				editor.commit();
			} else if (now - lastHandleTimeMillis < 0) {
				//设定下一次启动时间
				startTimedService(timeInterval);
				//校准请求时间
				SharedPreferences.Editor editor = sharedPrefrences.edit();
				editor.putLong(lastTimePrefName, now);
				editor.commit();
			} else {
				long nextTime = timeInterval - (now - lastHandleTimeMillis);
				startTimedService(nextTime);
			}
		} else {
			//设定下一次启动时间
			startTimedService(timeInterval);
			HLog.i(TAG, "onStartCommand: invalid request");
		}
		if(mIsDeamonService) {
			return START_STICKY;
		} else {
			return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
		}
	}
	
	@SuppressLint("NewApi")
	protected void stopThread() {
		HLog.i(TAG, "to stop thread: " + this.getClass());
		if(mLooper != null) {
			if(Build.VERSION.SDK_INT >= 18) {
				mLooper.quitSafely();
			} else {
				mLooper.quit();
			}
			mLooper = null;
		}
	}
	
	@Override
	public void onDestroy() {
		HLog.i(TAG, "onDestroy TimedService");
		stopThread();
		super.onDestroy();
	}
	
	/**
	 * 在delay秒之后发起请求
	 * 
	 * @param delay 等待下一次启动定位请求的时间，单位豪秒
	 */
	private void startTimedService(long delay) {
		HLog.i(TAG, "now: + " + DateTool.dateToString(
				new Date(), DateTool.D_T_FORMAT) + ", start " + this.getClass() + " after: " + delay);
		AlarmManager alarmManager = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, this.getClass());
		PendingIntent operation = PendingIntent.getService(this, 1, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		if(Build.VERSION.SDK_INT >= 19) {
			alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + delay, operation);
		} else {
			alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + delay, operation);
		}
	}

	protected void stopTimedService() {
		AlarmManager alarmManager = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, this.getClass());
		PendingIntent operation = PendingIntent.getService(this, 1, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(operation);
		stopSelf();
	}
	
	public static void stopTimedService(Context context, String serviceAction) {
		Intent service = new Intent();
		service.setAction(serviceAction);
		service.putExtra(TimedService.COMMAND_KEY, TimedService.STOP_COMMAND);
		context.startService(service);
	}
	
	public static void stopTimedService(Context context, Class<?> serviceClass) {
		Intent service = new Intent(context, serviceClass);
		service.putExtra(TimedService.COMMAND_KEY, TimedService.STOP_COMMAND);
		context.startService(service);
	}

}
