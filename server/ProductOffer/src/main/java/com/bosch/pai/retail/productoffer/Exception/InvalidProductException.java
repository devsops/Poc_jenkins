package com.bosch.pai.retail.productoffer.Exception;

public class InvalidProductException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2150314441742161080L;
	
	private String message;
	private int statusCode;
	
	public InvalidProductException(String message){
		super(message);
		this.setMessage(message);
	}
	
	public InvalidProductException(String message, Throwable ex){
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