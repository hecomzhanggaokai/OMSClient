package com.hecom.omsclient.server;

import android.content.Context;


/**
 * 业务操作基类
 * 
 * @author Android
 * 
 */
public abstract class BaseHandler {

	/**
	 * 数据库操作类
	 */
//	public DbOperator mDbOperator;

	/**
	 * 上下文
	 */
	public Context mContext;

	/**
	 * 业务处理返回监听
	 */
	public IHandlerListener mHandlerListener;

	/**
	 * 操作记录
	 */
//	public OperatorRecordHandler operatorHandler = null;

	/**
	 * 设置监听
	 * 
	 * @param mHandlerListener
	 */
	public void setmHandlerListener(IHandlerListener mHandlerListener) {
		this.mHandlerListener = mHandlerListener;
	}

	/**
	 * 获取监听
	 * 
	 * @return
	 */
	public IHandlerListener getmHandlerListener() {
		return mHandlerListener;
	}

	public BaseHandler(Context context) {
		mContext = context;
//		mDbOperator = DbOperator.getInstance(context);
//		operatorHandler = OperatorRecordHandler.getInstance(context);
	}

	public BaseHandler(Context context, IHandlerListener iHandlerListener) {
		this(context);
		mHandlerListener = iHandlerListener;
	}

	/**
	 * 业务监听返回
	 * 
	 * @author Android
	 * 
	 */
	public interface IHandlerListener {
		public <T> void onHandlerListener(T t);
	}
}
