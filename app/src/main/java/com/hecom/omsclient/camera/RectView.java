package com.hecom.omsclient.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hecom.omsclient.utils.Tools;


/**
 * @ClassName: RectView
 * @Description: TODO( )
 * @author 钟航
 * @date 2014年12月31日 下午3:16:07
 */
public class RectView extends View {
	private int mWidth;
	private int mHeight;
	private Paint mPaintWhite;
	private Paint mPaintGreen;
	private boolean isDrawWhiteRect = false;
	private boolean isDrawGreenRect = false;
	private int mRectWidth;
	private int mRectStrokeWidth;
	private float x, y;
	public static final int DRAW_WHITE_RECT = 0X32;
	public static final int DRAW_GREEN_RECT = 0X33;
	int count = 0;
	private OnClick listener;

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setListener(OnClick listener) {
		this.listener = listener;
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			invalidate();
			switch (msg.what) {
			case DRAW_WHITE_RECT:

				count++;
				if (count < 2) {
					handler.sendEmptyMessageDelayed(DRAW_WHITE_RECT, 300);
				} else {
					isDrawWhiteRect = false;
					isDrawGreenRect = true;
					handler.sendEmptyMessageDelayed(DRAW_GREEN_RECT, 300);
					count = 0;
				}
				break;
			case DRAW_GREEN_RECT:
				count++;
				if (count < 2) {
					handler.sendEmptyMessageDelayed(DRAW_GREEN_RECT, 300);
				} else {
					isDrawWhiteRect = false;
					isDrawGreenRect = false;
					count = 0;
				}
				break;
			default:
				break;
			}
		};
	};

	/**
	 * @param context
	 * @param attrs
	 */
	public RectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRectStrokeWidth = Tools.dip2px(context, 1);
		mPaintWhite = new Paint();
		mPaintWhite.setColor(Color.WHITE);
		mPaintWhite.setStrokeWidth(mRectStrokeWidth);
		mPaintWhite.setStyle(Paint.Style.STROKE);
		mPaintGreen = new Paint();
		mPaintGreen.setColor(Color.GREEN);
		mPaintGreen.setStrokeWidth(mRectStrokeWidth);
		mPaintGreen.setStyle(Paint.Style.STROKE);
		mRectWidth = Tools.dip2px(context, 100);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
	}

	public void showRect() {
		x = mWidth / 2;
		y = mHeight / 2;
		isDrawWhiteRect = true;
		handler.sendEmptyMessage(DRAW_WHITE_RECT);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x = event.getX();
			y = event.getY();
			isDrawWhiteRect = true;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (listener != null) {
				listener.onClick();
			}
			handler.sendEmptyMessage(DRAW_WHITE_RECT);
			break;
		default:
			break;
		}
		invalidate();
		return true;
	}

	private void drawRect(Canvas canvas, float x, float y, Paint paint) {
		canvas.drawLine(x - mRectWidth / 2, y - mRectWidth / 2, x - mRectWidth / 2, y - mRectWidth
				/ 4, paint);
		canvas.drawLine(x - mRectWidth / 2, y + mRectWidth / 2, x - mRectWidth / 2, y + mRectWidth
				/ 4, paint);
		canvas.drawLine(x + mRectWidth / 2, y - mRectWidth / 2, x + mRectWidth / 2, y - mRectWidth
				/ 4, paint);
		canvas.drawLine(x + mRectWidth / 2, y + mRectWidth / 2, x + mRectWidth / 2, y + mRectWidth
				/ 4, paint);
		canvas.drawLine(x - mRectWidth / 2, y - mRectWidth / 2, x - mRectWidth / 4, y - mRectWidth
				/ 2, paint);
		canvas.drawLine(x - mRectWidth / 2, y + mRectWidth / 2, x - mRectWidth / 4, y + mRectWidth
				/ 2, paint);
		canvas.drawLine(x + mRectWidth / 2, y - mRectWidth / 2, x + mRectWidth / 4, y - mRectWidth
				/ 2, paint);
		canvas.drawLine(x + mRectWidth / 2, y + mRectWidth / 2, x + mRectWidth / 4, y + mRectWidth
				/ 2, paint);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isDrawWhiteRect)
			drawRect(canvas, x, y, mPaintWhite);
		if (isDrawGreenRect)
			drawRect(canvas, x, y, mPaintGreen);
	}

	interface OnClick {
		public void onClick();
	}
}
