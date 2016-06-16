package com.hecom.omsclient.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.hecom.omsclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 钟航
 * @version 1.0 创建时间：2014年11月5日 下午6:18:52 类的用途：
 */
public class AlertDialogWidget {
	private Context mActivity;
	private static AlertDialogWidget mWidget;
	private LayoutInflater mInflater;
	private Handler mHandler = new Handler();
	private Runnable mDismissTask = new Runnable() {
		public void run() {
			Log.i("Test", "to do dismissTask");
			dismissProgressDialog();
		}
	};

	private AlertDialogWidget(Context activity) {
		mActivity = activity;
		mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setCancelable(boolean flag) {
		if (progressDialog != null)
			progressDialog.setCancelable(flag);
	}

	public static AlertDialogWidget getInstance(Context activity) {
		if (mWidget == null) {
			mWidget = new AlertDialogWidget(activity);
		} else {
			if (mWidget.mActivity != activity) {
				if (mWidget.progressDialog != null
						&& mWidget.progressDialog.isShowing()) {
					try {
						mWidget.progressDialog.dismiss();
						//userTrackClose();
					} catch (Exception e) {
					} finally {
						mWidget.progressDialog = null;
					}
				}
				mWidget = new AlertDialogWidget(activity);
			}

		}
		return mWidget;
	}

	public boolean isProgressShowing() {
		return progressDialog != null && progressDialog.isShowing();
	}

	/***
	 * 带有标题和内容的弹出窗
	 *
	 * @param title
	 *            标题
	 * @param msg
	 *            内容
	 * @return
	 */
	public Dialog  createProgressDialog(final String title, String msg) {
		if (progressDialog != null && progressDialog.isShowing()) {
			return progressDialog;
		}
		progressDialog = new Dialog(mActivity, R.style.DialogNoTitle);
		progressDialog.setCancelable(false);
		View v = mInflater.inflate(R.layout.pop_progress, null);
		TextView tvMessage = (TextView) v.findViewById(R.id.textview_message);
		tvMessage.setText(msg);
		progressDialog.setContentView(v);

		//userTrackType("进度", title);
//		UserTrack.showDialog(title);
		progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
//				UserTrack.closeDialog(title);
			}
		});
		progressDialog.show();
		return progressDialog;
	}


	public static interface OnPageSelect{
		void onSelect(int page);
		void onDismiss();
	}
//
//	public Dialog createCompaignDialog(List<Compaign> list, String buttonText, final OnPageSelect pageSelect){
//
//		if(list==null || list.size()<=0){
//			 throw  new IllegalArgumentException("list 不能为空");
//		}
//
//		pageViews=new ArrayList<View>();
//
//		LayoutInflater inflater=(LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		for(int i=0;i<list.size();i++) {
//			ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.dialog_viewpage_contnet, null);
//			ImageView imageView=(ImageView)viewGroup.findViewById(R.id.compaign_imageView);
//			TextView headView=(TextView)viewGroup.findViewById(R.id.compaign_head);
//			TextView contentView=(TextView)viewGroup.findViewById(R.id.compaign_content);
//
//			Compaign compaign=list.get(i);
//
//			String head=compaign.getHeader();
//			if(!TextUtils.isEmpty(head)){
//				headView.setText(head);
//				headView.setVisibility(View.VISIBLE);
//			}
//
//
//			String content=compaign.getContent();
//			if(!TextUtils.isEmpty(content)){
//				contentView.setText(content);
//				contentView.setVisibility(View.VISIBLE);
//			}
//
//			String imageUrl=compaign.getImg();
//			if(!TextUtils.isEmpty(imageUrl)){
//				SOSApplication.getGlobalImageLoader().displayImage(imageUrl, imageView, ImageOptionsFactory.getCircleBitmapOption(Tools.dip2px(
//						mActivity, 106),R.drawable.default_image));
//			}
//
//			pageViews.add(viewGroup);
//		}
//
//
//		return createViewPagerDialog(buttonText, pageSelect);
//
//
//	}


	/***
	 * 带有标题和内容的弹出窗
	 *
	 * @return
	 */
//	private Dialog createViewPagerDialog(final String buttonText, final OnPageSelect onPageSelect) {
//		if (progressDialog != null && progressDialog.isShowing()) {
//			progressDialog.dismiss();
//		}
//		progressDialog = new Dialog(mActivity, R.style.DialogNoTitle);
//		progressDialog.setCancelable(true);
//		progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				onPageSelect.onDismiss();
////				UserTrack.closeDialog("可左右滑动切换显示对话框");
//			}
//		});
//
//		View root = mInflater.inflate(R.layout.dialog_pageview_plugin,
//				null);
//
//		imageViews = new ImageView[pageViews.size()];
//		ViewPager viewPager = (ViewPager) root.findViewById(R.id.guidePages);
//		ViewGroup group = (ViewGroup) root.findViewById(R.id.viewGroup);
//
//		for (int i = 0; i < pageViews.size(); i++) {
//
//			ImageView imageView = new ImageView(mActivity);
//			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//					Tools.dip2px(mActivity, 6), Tools.dip2px(mActivity, 6));
//			layoutParams.setMargins(Tools.dip2px(mActivity, 4), 0, 0,
//					Tools.dip2px(mActivity, 21));
//			imageView.setLayoutParams(layoutParams);
//			imageView.setPadding(Tools.dip2px(mActivity, 12), 8,
//					Tools.dip2px(mActivity, 12), 8);
//			imageViews[i] = imageView;
//
//			if (i == 0) {
//				// 默认选中第一张图片
//				imageViews[i]
//						.setBackgroundResource(R.drawable.slider_current);
//			} else {
//				imageViews[i]
//						.setBackgroundResource(R.drawable.slider_default);
//			}
//
//			group.addView(imageViews[i]);
//
//			viewPager.setAdapter(new GuidePageAdapter());
//			viewPager.setOnPageChangeListener(new GuidePageChangeListener());
//		}
//
//		final TextView sure_button = (TextView) root
//				.findViewById(R.id.sure_button);
//		sure_button.setText(buttonText);
//
//		currentPage=0;
//		sure_button.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onPageSelect.onSelect(currentPage);
//			}
//		});
//
//		progressDialog.setContentView(root);
//		UserTrack.showDialog("可左右滑动切换显示对话框");
//		progressDialog.show();
//		return progressDialog;
//	}

	/***
	 * 带有标题和内容的弹出窗,确认取消
	 *
	 * @return
	 */
//	public Dialog createCustomProgressDialog(int layoutId, final String titleStr,
//			final String contentStr,final String confirmStr, OnClickListener buttonOnClickListener) {
//		if (progressDialog != null && progressDialog.isShowing()) {
//			return progressDialog;
//		}
//		progressDialog = new Dialog(mActivity, R.style.DialogNoTitle);
//		progressDialog.setCancelable(true);
//		View root = mInflater.inflate(R.layout.dialog_pageview_window_custom_pager,
//				null);
//
//		final TextView btn_confirm_dialog = (TextView) root.findViewById(R.id.btn_confirm_dialog);
//		final TextView btn_cancel_dialog = (TextView) root.findViewById(R.id.btn_cancel_dialog);
//		final ImageView module_icon = (ImageView) root.findViewById(R.id.module_icon);
//		final TextView module_title = (TextView) root.findViewById(R.id.module_title);
//		final TextView module_description = (TextView) root.findViewById(R.id.module_description);
//		module_icon.setImageResource(layoutId);
//		module_title.setText(titleStr);
//		module_description.setText(contentStr);
//
//		btn_confirm_dialog.setText(confirmStr);
//		if (buttonOnClickListener != null) {
//			btn_confirm_dialog.setOnClickListener(buttonOnClickListener);
//		} else {
//			btn_confirm_dialog.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					dismissProgressDialog();
//					UserTrack.closeDialog("自定义进度对话框");
//				}
//			});
//		}
//
//		btn_cancel_dialog.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dismissProgressDialog();
//				UserTrack.closeDialog("自定义进度对话框");
//			}
//		});
//
//		progressDialog.setContentView(root);
//		UserTrack.showDialog("自定义进度对话框");
//
//		progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				UserTrack.closeDialog("自定义进度对话框");
//			}
//		});
//
//		progressDialog.show();
//		return progressDialog;
//	}

//	public class GoToSystemSettingOnClickListener implements OnClickListener {
//
//		@Override
//		public void onClick(View v) {
//			if(mActivity!=null) {
//				Intent intent = new Intent(Settings.ACTION_SETTINGS);
//				mActivity.startActivity(intent);
//				dismissProgressDialog();
//			}
//		}
//	}

//	public Dialog createCustomProgressDialog(int[] layoutIds,
//											 final String buttonText,
//											 OnClickListener buttonOnClickListener) {
//		return createCustomProgressDialog(layoutIds,buttonText,buttonOnClickListener,true);
//	}

	/***
	 * 带有标题和内容的弹出窗,带viewpager
	 *
	 * @return
	 */
//	public Dialog createCustomProgressDialog(int[] layoutIds,
//			final String buttonText, OnClickListener buttonOnClickListener,boolean showSelectedPoint) {
//		if (progressDialog != null && progressDialog.isShowing()) {
//			return progressDialog;
//		}
//		progressDialog = new Dialog(mActivity, R.style.DialogNoTitle);
//		progressDialog.setCancelable(true);
//		View root = mInflater.inflate(R.layout.dialog_pageview_window_custom,
//				null);
//		// TextView tvMessage = (TextView)
//		// v.findViewById(R.id.textview_message);
//		// tvMessage.setText(msg);
//
//		RelativeLayout layout = (RelativeLayout) root
//				.findViewById(R.id.centerLayout);
//		root.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//			}
//		});
//		final TextView sure_button = (TextView) root
//				.findViewById(R.id.sure_button);
//		sure_button.setText(buttonText);
//		if (buttonOnClickListener != null) {
//			sure_button.setOnClickListener(buttonOnClickListener);
//		} else {
//			sure_button.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					sure_button.setText(buttonText + buttonText);
//					// dismissProgressDialog();
//				}
//			});
//		}
//
//		ViewPager viewPager;
//		ViewGroup group;
//		ImageView imageView;
//		LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		if (layoutIds != null && layoutIds.length > 0) {
//			pageViews = new ArrayList<View>();
//			for (int i = 0; i < layoutIds.length; i++) {
//				pageViews.add(inflater.inflate(layoutIds[i], null));
//			}
//
//			imageViews = new ImageView[pageViews.size()];
//			viewPager = (ViewPager) root.findViewById(R.id.guidePages);
//			group = (ViewGroup) root.findViewById(R.id.viewGroup);
//
//			for (int i = 0; i < pageViews.size(); i++) {
//
//				imageView = new ImageView(mActivity);
//				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//						Tools.dip2px(mActivity, 6), Tools.dip2px(mActivity, 6));
//				layoutParams.setMargins(Tools.dip2px(mActivity, 4), 0, 0,
//						Tools.dip2px(mActivity, 21));
//				imageView.setLayoutParams(layoutParams);
//				imageView.setPadding(Tools.dip2px(mActivity, 12), 8,
//						Tools.dip2px(mActivity, 12), 8);
//				imageViews[i] = imageView;
//
//				if (i == 0) {
//					// 默认选中第一张图片
//					imageViews[i]
//							.setBackgroundResource(R.drawable.slider_current);
//				} else {
//					imageViews[i]
//							.setBackgroundResource(R.drawable.slider_default);
//				}
//
//				group.addView(imageViews[i]);
//			}
//			if(!showSelectedPoint) {
//				group.setVisibility(View.GONE);
//			}
//			viewPager.setAdapter(new GuidePageAdapter());
//			viewPager.setOnPageChangeListener(new GuidePageChangeListener());
//		}
//
//		progressDialog.setContentView(root);
//		UserTrack.showDialog("自定义进度对话框");
//
//		progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				UserTrack.closeDialog("自定义进度对话框");
//			}
//		});
//
//		progressDialog.show();
//		return progressDialog;
//	}

	ArrayList<View> pageViews;
	ImageView[] imageViews;

	// 指引页面数据适配器
	private class GuidePageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(pageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(pageViews.get(arg1));
			return pageViews.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}

	private static int currentPage=0;

	// 指引页面更改事件监听器
//	private class GuidePageChangeListener implements OnPageChangeListener {
//
//		@Override
//		public void onPageScrollStateChanged(int arg0) {
//
//		}
//
//		@Override
//		public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//		}
//
//		@Override
//		public void onPageSelected(int arg0) {
//
//			currentPage=arg0;
//			for (int i = 0; i < imageViews.length; i++) {
//				imageViews[arg0]
//						.setBackgroundResource(R.drawable.slider_current);
//
//				if (arg0 != i) {
//					imageViews[i]
//							.setBackgroundResource(R.drawable.slider_default);
//				}
//			}
//		}
//	}

	/***
	 * 带有标题和内容的弹出窗(取消回调)
	 *
	 * @param title
	 *            标题
	 * @param msg
	 *            内容
	 */
//	public void createProgressDialog(final String title, String msg,
//			final OnCancelListener listener) {
//		if (progressDialog != null && progressDialog.isShowing()) {
//			return;
//		}
//		progressDialog = new Dialog(mActivity, R.style.DialogNoTitle);
//		progressDialog.setCancelable(false);
//		progressDialog
//				.setOnCancelListener(new DialogInterface.OnCancelListener() {
//					@Override
//					public void onCancel(DialogInterface dialog) {
//						if (listener != null) {
//							listener.onCancel();
//						}
//					}
//				});
//
//		progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				UserTrack.closeDialog(title);
//			}
//		});
//
//		View v = mInflater.inflate(R.layout.pop_progress, null);
//		TextView tvMessage = (TextView) v.findViewById(R.id.textview_message);
//		tvMessage.setText(msg);
//		progressDialog.setContentView(v);
//		UserTrack.showDialog(title);
//		progressDialog.show();
//	}
//
//
//	private static void userTrackClose(){
//		UserTrack.closeDialog("对话框");
//	}
//
//	private static void userTrackClose(String dialogName){
//		UserTrack.closeDialog(dialogName);
//	}

	private Dialog progressDialog;

	/**
	 * 生成带有progress的弹出窗，只有内容
	 *
	 * @param msg
	 */
	public Dialog createProgressDialog(String msg) {
		Dialog dialog = createProgressDialog("温馨提示", msg);
		mHandler.removeCallbacks(mDismissTask);
		mHandler.postDelayed(mDismissTask, 50 * 1000);
		return dialog;
	}

	public void dismissProgressDialog() {
		Log.i("Test", "to dismissProgressDialog");
		mHandler.removeCallbacks(mDismissTask);
		if (progressDialog != null && progressDialog.isShowing()
				&& mActivity != null) {
			try {
				progressDialog.dismiss();
				mActivity=null;
                mInflater = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			progressDialog = null;
		}
	}

	/**
	 * 弹出对话框，没有按钮的
	 *
	 * @param title
	 * @param msg
	 */
	public void createAlertDialog(String title, String msg) {
		initDialog(title, msg, null, null, null, null, false,true);
	}

	public void createAlertDialog(String title, String msg,
			String btnMsg, PopupDialogClick listener) {
		initDialog(title, msg, btnMsg, listener, null, null, false,false);
	}

	public void createAlertDialog(String title, String msg,
			String btn1Msg, PopupDialogClick listener1, String btn2Msg,
			PopupDialogClick listener2) {
		initDialog(title, msg, btn1Msg, listener1, btn2Msg, listener2, false,false);
	}

	/**
	 * 弹出对话框，没有按钮的
	 *
	 * @param title
	 * @param msg
	 */
	public void createAlertDialogLandspace(String title, String msg) {
		initDialog(title, msg, null, null, null, null, true,true);
	}

	public void createAlertDialogLandspace(String title, String msg,
			String btnMsg, PopupDialogClick listener) {
		initDialog(title, msg, btnMsg, listener, null, null, true,false);
	}

	public void createAlertDialogLandspace(String title, String msg,
			String btn1Msg, PopupDialogClick listener1, String btn2Msg,
			PopupDialogClick listener2) {
		initDialog(title, msg, btn1Msg, listener1, btn2Msg, listener2, true,false);
	}

	public interface PopupDialogClick {
		public void onDialogBottonButtonClick();
	}

	private void initDialog(String title, String msg, String btn1Msg,
			final PopupDialogClick listener1, String btn2Msg,
			final PopupDialogClick listener2, boolean isLandspace,boolean cancelable) {
		DialogContent.getInstances().setBtn1Msg(btn1Msg);
		DialogContent.getInstances().setBtn2Msg(btn2Msg);
		DialogContent.getInstances().setTitle(title);
		DialogContent.getInstances().setMsg(msg);
		DialogContent.getInstances().setListener1(listener1);
		DialogContent.getInstances().setListener2(listener2);
        DialogContent.getInstances().setCancelable(cancelable);
		if (mActivity != null) {
			Configuration cf = mActivity.getResources().getConfiguration();
			if (cf.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				DialogContent.getInstances().setLandSpace(true);
			} else
				DialogContent.getInstances().setLandSpace(false);
		} else {
			DialogContent.getInstances().setLandSpace(isLandspace);
		}
		Intent intent = new Intent(mActivity, AlertActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mActivity.startActivity(intent);
	}

	// /**
	// * dialog title 和 msg 不能为空
	// *
	// * @param title
	// * @param msg
	// * @param btn1
	// * @param click1
	// * @param btn2
	// * @param click2
	// */
	// public void showAlertDialog(String title, String msg, String btn1,
	// final PopupDialogClick click1, String btn2, final PopupDialogClick
	// click2) {
	// final Dialog dialog = new Dialog(mActivity, R.style.DialogNoTitle);
	// View v = mInflater.inflate(R.layout.my_dialog_layout, null);
	// TextView tvHeader = (TextView) v.findViewById(R.id.tv_dialog_title);
	// tvHeader.setText(title);
	// TextView tvMsg = (TextView) v.findViewById(R.id.tv_dialog_message);
	// tvMsg.setText(msg);
	// // 按钮1和按钮2都为空 则隐藏底部
	// if (btn1 == null && btn2 == null) {
	// v.findViewById(R.id.h_line).setVisibility(View.GONE);
	// v.findViewById(R.id.ll_btn).setVisibility(View.GONE);
	// } else {
	// // 按钮1
	// Button button1 = (Button) v.findViewById(R.id.btnCancel);
	// if (btn1 == null) {
	// button1.setVisibility(View.GONE);
	// v.findViewById(R.id.v_line).setVisibility(View.GONE);
	// } else {
	// button1.setText(btn1);
	// button1.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// click1.onDialogBottonButtonClick();
	// dialog.cancel();
	// }
	// });
	// }
	// // 按钮2
	// Button button2 = (Button) v.findViewById(R.id.btnOk);
	// if (btn2 == null) {
	// button2.setVisibility(View.GONE);
	// v.findViewById(R.id.v_line).setVisibility(View.GONE);
	// } else {
	// button2.setText(btn2);
	// button2.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// click2.onDialogBottonButtonClick();
	// dialog.cancel();
	// }
	// });
	// }
	// }
	// dialog.setContentView(v);
	// dialog.setCanceledOnTouchOutside(false);
	// dialog.setCancelable(false);
	// // Window dialogWindow = dialog.getWindow();
	// // WindowManager m = ((Activity) mActivity).getWindowManager();
	// // Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
	// // WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
	// 获取对话框当前的参数值
	// // p.height = (int) WindowManager.LayoutParams.WRAP_CONTENT; //
	// // p.width = (int) DeviceTools.dip2px(mActivity, 280f); // 宽度设置为屏幕的0.8
	// // dialogWindow.setAttributes(p);
	// dialog.show();
	// }

	/**
	 * 显示单选框的弹出窗
	 *
	 * @param <T>
	 * @param select
	 *            默认选择 SELECT_NO_ONE默认不选择
	 * @param title
	 *            显示的标题
	 * @param data
	 *            显示的内容
	 */
//	public <T> void showSingleAlert(final String title, int select, List<T> data,
//			final OnSelectListener os) {
//		// 自定义dialog
//		final Dialog dialog = new Dialog(mActivity, R.style.DialogNoTitle);
//		View v = mInflater.inflate(R.layout.pop_sigle_select_dialog, null);
//		TextView tvHeader = (TextView) v.findViewById(R.id.tvTitle);
//		if (TextUtils.isEmpty(title)) {
//            tvHeader.setVisibility(View.GONE);
//        } else {
//            tvHeader.setVisibility(View.VISIBLE);
//            tvHeader.setText(title);
//        }
//		ListView lv = (ListView) v.findViewById(R.id.lvData);
//		if (data.size() > 7) {
//			LayoutParams lp = lv.getLayoutParams();
//			lp.height = DeviceTools.dip2px(mActivity, 350f);
//			lv.setLayoutParams(lp);
//		}
//		final AlertSingleAdapter<T> adapter = new AlertSingleAdapter<T>(select,
//				data, mInflater);
//		lv.setAdapter(adapter);
//		lv.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				adapter.selectPosition(position);
//				os.onSelect(position);
//				new Handler().postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						dialog.dismiss();
//					}
//				}, 200);
//			}
//		});
//
//		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				UserTrack.closeDialog(title);
//			}
//		});
//
//		dialog.setContentView(v);
//		UserTrack.showDialog(title);
//		dialog.show();
//	}
//
//
//	public static void showRoundDialog(Context context,String dialogName,View view,boolean cancellable, DialogActivity.OnDismissListener dismissListener,DialogActivity.OnCancellListener cancelListener){
//		DialogActivity.DialogSettings dialogSettings=new DialogActivity.DialogSettings();
//		dialogSettings.view=view;
//		dialogSettings.mCancellable=cancellable;
//		dialogSettings.mCancelListener=cancelListener;
//		dialogSettings.mDismissListener=dismissListener;
//		dialogSettings.userTrackName=dialogName;
//		DialogActivity.setSettings(dialogSettings);
//
//		Intent intent=new Intent(context,DialogActivity.class);
//		context.startActivity(intent);
//	}
//
//	public static <T> void showSelectSingle(Context context, String title, List<T> data,
//			final OnSelectListener listener){
//		LayoutInflater inflater=LayoutInflater.from(context);
//		View v = inflater.inflate(R.layout.dialog_select_single, null);
//		TextView tvHeader = (TextView) v.findViewById(R.id.tvTitle);
//        if (TextUtils.isEmpty(title)) {
//            tvHeader.setVisibility(View.GONE);
//        } else {
//            tvHeader.setVisibility(View.VISIBLE);
//            tvHeader.setText(title);
//        }
//		ListView lv = (ListView) v.findViewById(R.id.lvData);
//		if (data.size() > 7) {
//			LayoutParams lp = lv.getLayoutParams();
//			lp.height = DeviceTools.dip2px(context, 350f);
//			lv.setLayoutParams(lp);
//		}
//		final AlertSingleAdapter<T> adapter = new AlertSingleAdapter<T>(-1,
//				data, inflater){
//			@Override
//			protected int getItemLayoutId() {
//				return R.layout.alert_sigle_item_center;
//			}
//		};
//		lv.setAdapter(adapter);
//		lv.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				adapter.selectPosition(position);
//				listener.onSelect(position);
//				DialogActivity.close();
//			}
//		});
//		showRoundDialog(context,"多选一对话框",v,false,null,null);
//	}
//
//    public <T> void showSelectSingleDialog(Context context, String title, List<T> data,
//                                            final OnSelectListener os){
//
//        // 自定义dialog
//        final Dialog dialog = new Dialog(context, R.style.DialogNoTitle);
//        View v = mInflater.inflate(R.layout.pop_sigle_select_dialog, null);
//        TextView tvHeader = (TextView) v.findViewById(R.id.tvTitle);
//        if (TextUtils.isEmpty(title)) {
//            tvHeader.setVisibility(View.GONE);
//        } else {
//            tvHeader.setVisibility(View.VISIBLE);
//            tvHeader.setText(title);
//        }
//        ListView lv = (ListView) v.findViewById(R.id.lvData);
//        if (data.size() > 7) {
//            LayoutParams lp = lv.getLayoutParams();
//            lp.height = DeviceTools.dip2px(mActivity, 350f);
//            lv.setLayoutParams(lp);
//        }
//        final AlertSingleAdapter<T> adapter = new AlertSingleAdapter<T>(-1,
//                data, mInflater){
//            @Override
//            protected int getItemLayoutId() {
//                return R.layout.alert_sigle_item_center;
//            }
//        };
//        lv.setAdapter(adapter);
//        lv.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                adapter.selectPosition(position);
//                os.onSelect(position);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.dismiss();
//                    }
//                }, 200);
//            }
//        });
//
//        dialog.setContentView(v);
//        UserTrack.showDialog(title);
//        dialog.show();
//    }
//
//
//	/**
//	 * 多选Dialog
//	 *
//	 * @param title
//	 *            标题
//	 * @param selected
//	 *            默认状态
//	 * @param data
//	 *            显示的数据
//	 * @param cb
//	 *            注册的监听器
//	 */
//	public <T> void createMultiChoiseDialog(final String title, boolean[] selected,
//			List<T> data, final CheckBoxListener cb) {
//		final Dialog dialog = new Dialog(mActivity, R.style.DialogNoTitle);
//		View v = mInflater.inflate(R.layout.alert_multichoice_dialog, null);
//		TextView tvHeader = (TextView) v.findViewById(R.id.tvTitle);
//		tvHeader.setText(title);
//		ListView lv = (ListView) v.findViewById(R.id.lvData);
//		if (selected.length > 7) {
//			LayoutParams lp = lv.getLayoutParams();
//			lp.height = DeviceTools.dip2px(mActivity, 350f);
//			lv.setLayoutParams(lp);
//		}
//		Button btnCancel = (Button) v.findViewById(R.id.btnCancel);
//		Button btnDerButton = (Button) v.findViewById(R.id.btnDetermin);
//		final MultiChoiceAdapter<T> adapter = new MultiChoiceAdapter<T>(
//				selected, data, mInflater);
//		lv.setAdapter(adapter);
//		lv.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				adapter.selectPosition(position);
//				// cb.checkBoxChanged(position);
//			}
//		});
//		btnCancel.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//				cb.onNegativeClick();
//			}
//		});
//		btnDerButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//				cb.onPositiveClick();
//			}
//		});
//		dialog.setContentView(v);
//		UserTrack.showDialog(title);
//		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				UserTrack.closeDialog(title);
//			}
//		});
//		dialog.show();
//	}
//
//	/**
//	 * 多项选择器监听
//	 */
//	public interface CheckBoxListener {
//		/** 选中状态改变 */
//		public void checkBoxChanged(int which);
//
//		/** 确定事件 */
//		public void onPositiveClick();
//
//		/** 取消事件 */
//		public void onNegativeClick();
//	}
//
//	/** 单选弹出框的选择 */
//	public interface OnSelectListener {
//		public void onSelect(int position);
//	}
//
//	/** 单选弹出框的选择 */
//	public interface OnCancelListener {
//		public void onCancel();
//	}

}
