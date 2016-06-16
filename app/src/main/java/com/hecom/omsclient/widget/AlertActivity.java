package com.hecom.omsclient.widget;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hecom.omsclient.R;
import com.hecom.omsclient.utils.Tools;

/**
 * @author 钟航
 * @ClassName: AlertActivity
 * @Description: TODO()
 * @date 2014年12月16日 上午10:07:45
 */
public class AlertActivity extends Activity/* extends UserTrackActivity*/ {
	AlertDialogWidget.PopupDialogClick listener1;
	AlertDialogWidget.PopupDialogClick listener2;
	Handler handler = new Handler();

	private String userTrackTitle="对话框";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pop_dialog);
        setFinishOnTouchOutside(DialogContent.getInstances().isCancelable());
		if (DialogContent.getInstances().isLandSpace()) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		String title = DialogContent.getInstances().getTitle();
		String msg = DialogContent.getInstances().getMsg();
		final String btn1Msg = DialogContent.getInstances().getBtn1Msg();
		final String btn2Msg = DialogContent.getInstances().getBtn2Msg();
		listener1 = DialogContent.getInstances().getListener1();
		listener2 = DialogContent.getInstances().getListener2();
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		TextView tvMessage = (TextView) findViewById(R.id.tvMessage);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		Button btnDerButton = (Button) findViewById(R.id.btnDetermin);
		View line = findViewById(R.id.line);
		if (btn1Msg == null) {
			btnCancel.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
			btnDerButton.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
		}
		if (btn2Msg == null) {
			btnCancel.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
			btnDerButton.setText(btn1Msg);
			btnDerButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertActivity.this.finish();
					if (listener1 != null) {
						// handler.post(new Runnable() {
						// public void run() {
						// listener1.onDialogBottonButtonClick();
						// }
						// });
						listener1.onDialogBottonButtonClick();

					}

				}
			});
		} else {
			btnCancel.setText(btn2Msg);
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertActivity.this.finish();
					if (listener2 != null) {
						// handler.post(new Runnable() {
						// public void run() {
						// listener2.onDialogBottonButtonClick();
						// }
						// });
						listener2.onDialogBottonButtonClick();
					}
				}
			});
			btnDerButton.setText(btn1Msg);
			btnDerButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertActivity.this.finish();
					if (listener1 != null) {
						// handler.post(new Runnable() {
						// public void run() {
						// listener1.onDialogBottonButtonClick();
						// }
						// });
						listener1.onDialogBottonButtonClick();
					}

				}
			});
		}
		if (title == null) {
			findViewById(R.id.ll_title).setVisibility(View.GONE);
		} else {
			tvTitle.setText(title);
		}
		if (!TextUtils.isEmpty(msg)) {
			if (getTextViewLength(tvMessage, msg) > Tools.dip2px(getApplicationContext(),
					240f)) {
				tvMessage.setGravity(Gravity.LEFT);
			} else {
				tvMessage.setGravity(Gravity.CENTER);
			}
			tvMessage.setText(msg);
		}

		//UserTrack
		String msgTitle=(title==null) ? "" : title;
/*		String msgTip;
		if(msg==null){
			msgTip="";
		}else{
			if(msg.length()>10){
				msgTip=msg.substring(0,10)+"...";
			}else{
				msgTip=msg;
			}
		}*/
		userTrackTitle=msgTitle;

	}

	public float getTextViewLength(TextView textView, String text) {
		TextPaint paint = textView.getPaint();
		// 得到使用该paint写上text的时候,像素为多少
		float textLength = paint.measureText(text);
		return textLength;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		return !DialogContent.getInstances().isCancelable();
//	}
}
