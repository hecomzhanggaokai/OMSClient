package com.sosgps.sosparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class SOSParserApi {
	
	
	public List<String> globalSet;
	private InputStream is;
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract Object analyze() throws Exception;
	
	public abstract void add();
	
	public void setIoObject(InputStream is){
		this.is = is;
	}

	public void closeIo(){
		if(is != null){
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
