package com.hecom.omsclient.utils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 脉可寻名片云识别任务
 * @author tianlupan 2016/1/7
 */
public class NameCardRecTask {

	/*public enum  ERROR_TYPE{
		TIMEOUT,NETWORK,FAIL;
	}

	public  interface OnCallback{
		void onError(ERROR_TYPE error);
		void onSuccess(String vcf);
		void onProgress(int progress);
	}


	private OnCallback mCallback;
	private RecognitionTask mRecognitionTask;
	private boolean isStarted=false;
	private static final String TAG=NameCardRecTask.class.getSimpleName();

	public void cancel(){
		if(mRecognitionTask!=null){
			mRecognitionTask.cancel(false);
		}
	}

	//这个池可用并可自动回收;
	private ExecutorService executor= new ThreadPoolExecutor(0, 1,
			1L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

	public void startTask(String imagePath,OnCallback callback){

		if(isStarted){
			throw new IllegalStateException("不用重复启用");
		}
		isStarted=true;

		if(TextUtils.isEmpty(imagePath)){
			throw new IllegalArgumentException();
		}

		if(callback==null){
			throw new IllegalArgumentException();
		}
		this.mCallback =callback;

		mRecognitionTask=new RecognitionTask();
		mRecognitionTask.executeOnExecutor(executor,imagePath);
		executor.shutdown();

	}

	private interface OnUpdateFinished{
		void onFinished();
	}

	private class  RecognitionTask extends AsyncTask<String,Integer,String> {

		private final  int UPLOAD_PROGRESS=70;
		private final int BEGIN_DELAY=1000;
		private final int MAX_DELAY=1000;
		private final int UPDATE_PROGRESS_INTERVAL=100;

		private boolean isFinished=false;

		private final int WAIT_FOR_MAX=99;
		private boolean isTimeout=false;

		float currentStep=0;

		private class UpdateThread extends Thread{

			private OnUpdateFinished updateFinished;

			private final int milliSeconds, from, to;
			private final boolean finishToReturn;

			public UpdateThread(int milliSeconds,int from,int to, boolean finishToReturn,OnUpdateFinished updateFinished)
			{
				setName("Update-NCR-Progress");
				this.milliSeconds=milliSeconds;
				this.from=from;
				this.to=to;
				this.finishToReturn=finishToReturn;
				this.updateFinished=updateFinished;
			}

			@Override
			public void run() {
				int count=milliSeconds / UPDATE_PROGRESS_INTERVAL;
				float step= (float)(to-from) / count;
				publishProgress(from);
				for(int i=0;i<count;i++){
					try {
						Thread.sleep(UPDATE_PROGRESS_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if(isCancelled()){
						return;
					}
					if(finishToReturn && isFinished){
						return;
					}
					currentStep+=step;
					publishProgress((int)currentStep);
				}
				if(updateFinished!=null){
					updateFinished.onFinished();
				}
			}
		}

		private void waitForAnimation(int milliSeconds,int to){
			Thread thread= new UpdateThread(milliSeconds,(int)currentStep,to,false,null);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		@Override
		protected String doInBackground(String... paths) {
			waitForAnimation(BEGIN_DELAY,UPLOAD_PROGRESS);
			String path=paths[0];
			File file = new File(path);
			Map<String, String> params = new HashMap<String, String>();
			// params.put("PIN", getIMEI(MainActivity.this));
			Map<String, File> files = new HashMap<String, File>();
			files.put("file1", file);

			Log.e(TAG,"开始上传识别...");
			publishProgress(UPLOAD_PROGRESS);

			new UpdateThread(FileUpload.READ_TIME_OUT, UPLOAD_PROGRESS, WAIT_FOR_MAX, true,
					new OnUpdateFinished() {
						@Override
						public void onFinished() {
							isTimeout=true;
							publishProgress(-1);
						}
					}).start();

			String resp = FileUpload.postFile(Config.getNameCardRecognitionUrl(), params, files);
			isFinished=true;
			// showProgress(MAX_DELAY,(int)currentStep,100,false);
			waitForAnimation(MAX_DELAY,100);
			publishProgress(100);
			// 发送
			return resp;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if(isCancelled()){
				return;
			}

			int progress=values[0];
			if(isTimeout){
				if(progress==-1){
					mCallback.onError(ERROR_TYPE.TIMEOUT);
				}
				return;
			}

			if(progress>=0 && progress<=100){
				mCallback.onProgress(progress);
			}
		}

		@Override
		protected void onPostExecute(String resp) {
			if(isCancelled() || isTimeout){
				Log.d(TAG,"超时或取消任务，名片扫描返回结果忽略...");
				return;
			}
			else if(resp.equals("error")) {
				mCallback.onError(ERROR_TYPE.NETWORK);
			} else if (resp.equals("fail")) {
				mCallback.onError(ERROR_TYPE.FAIL);
			}
			else {
				try {
					resp = new String(resp.getBytes("utf-8"));
					//showResult(activity,resp,path,type,contact_pos);
					JSONObject jsonObject = new JSONObject(resp);
					String data = jsonObject.getString("data");
					HLog.d("CamCard", "名片内容=" + data);
					String result=jsonObject.getString("result");
					//0代表成功，-1,-2代表失败
					if(result.equalsIgnoreCase("0") && !TextUtils.isEmpty(data)){
						mCallback.onSuccess(data);
					}else{
						mCallback.onError(ERROR_TYPE.FAIL);
					}
				}catch (JSONException e){
					mCallback.onError(ERROR_TYPE.FAIL);
				}
				catch (Exception e) {
					HLog.e("error", "handleMessage, errmsg=" + e.getMessage());
					e.printStackTrace();
				}
			}


		}


}*/
}
