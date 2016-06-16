package com.hecom.omsclient.widget;


/**
 * @ClassName: DialogContent
 * @Description: TODO(用于管理弹出窗上的数据)
 * @author 钟航
 * @date 2014年12月16日 上午10:12:34
 */
public class DialogContent {
	private static DialogContent content;
	private String title;
	private String msg;
	private boolean isLandSpace;

	private boolean cancelable=true;

	/**
	 * @return the isLandSpace
	 */
	public boolean isLandSpace() {
		return isLandSpace;
	}

    /**
	 * @param isLandSpace
	 *            the isLandSpace to set
	 */
	public void setLandSpace(boolean isLandSpace) {
		this.isLandSpace = isLandSpace;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return the btn1Msg
	 */
	public String getBtn1Msg() {
		return btn1Msg;
	}

	/**
	 * @param btn1Msg
	 *            the btn1Msg to set
	 */
	public void setBtn1Msg(String btn1Msg) {
		this.btn1Msg = btn1Msg;
	}

	/**
	 * @return the btn2Msg
	 */
	public String getBtn2Msg() {
		return btn2Msg;
	}

	/**
	 * @param btn2Msg
	 *            the btn2Msg to set
	 */
	public void setBtn2Msg(String btn2Msg) {
		this.btn2Msg = btn2Msg;
	}

	/**
	 * @return the listener1
	 */
	public AlertDialogWidget.PopupDialogClick getListener1() {
		return listener1;
	}

	/**
	 * @param listener1
	 *            the listener1 to set
	 */
	public void setListener1(AlertDialogWidget.PopupDialogClick listener1) {
		this.listener1 = listener1;
	}

	/**
	 * @return the listener2
	 */
	public AlertDialogWidget.PopupDialogClick getListener2() {
		return listener2;
	}

	/**
	 * @param listener2
	 *            the listener2 to set
	 */
	public void setListener2(AlertDialogWidget.PopupDialogClick listener2) {
		this.listener2 = listener2;
	}

	/**
	 * 是否可以点击外面取消
	 * @param cancelable
	 */
	public void setCancelable(boolean cancelable){
		this.cancelable=cancelable;
	}

	public boolean isCancelable(){
		return this.cancelable;
	}

	private String btn1Msg;
	private String btn2Msg;
	private AlertDialogWidget.PopupDialogClick listener1;
	private AlertDialogWidget.PopupDialogClick listener2;

	private DialogContent() {

	}

	public static DialogContent getInstances() {
		if (content == null) {
			content = new DialogContent();
		}
		return content;
	}
}
