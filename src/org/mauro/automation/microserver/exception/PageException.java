package org.mauro.automation.microserver.exception;

import java.util.logging.Logger;

import org.mauro.automation.LogUtils;

public class PageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LogUtils.loggerForThisClass();

	private final int code;
	private final String redirect;
	
	public PageException(int code) {
		this(code, null);
	}
	
	public PageException(int code, String redirect){
		this.code = code;
		this.redirect = redirect;
		log.info("risedexception "+code+" redirect "+redirect);
	}

	public int getCode(){
		return code;
	}
	
	public String getRedirect(){
		return redirect;
	}
}
