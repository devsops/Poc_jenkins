package com.bosch.pai.retail.configuration.Exception;

public class InvalidUrlException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2150314441742161080L;
	
	private String message;
	private int statusCode;
	
	public InvalidUrlException(String message){
		super(message);
		this.setMessage(message);
	}
	
	public InvalidUrlException(String message, Throwable ex){
		super(message, ex);
		this.setMessage(message);
	}
    @Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}