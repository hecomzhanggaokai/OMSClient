package com.hecom.omsclient.js.entity;

import android.text.TextUtils;

/**
 * 
 * @author HEcom
 * 
 */
public class ParamOfflineImage extends ParamBase {
	private String customerCode;
	private String imagePath;
	private String category;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Override
	public boolean isValid() {
		return (!TextUtils.isEmpty(customerCode) && !TextUtils.isEmpty(imagePath) && !TextUtils.isEmpty(category));
	}
}
