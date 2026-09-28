package com.cpt.payments.constant;

public enum ErrorCodeEnum {
	
	// create errorCode & errorMessage files as part of this enum errroCode "100001", ""Amount cannot be negative"
	GENERIC_ERROR("10000", "Unable to process request, please try again later"),
	INVALID_AMOUNT("10001", "Amount cannot be negative, please correct the amount and try again"),
	MERCHANT_TXN_REF_EMPTY("10002", "Merchant transaction reference is null or empty."),
	DUPLICATE_MERCHANT_TXN_REF("10003", "Duplicate entry for merchant payment request"),
	PAYMENT_NOT_SAVED("10004", "Unable to save payment in DB, please try again later"),
	PAYMENT_ATTEMPT_THRESHOLD_EXCEEDED("10005", "Payment attempts exceeded threshold, please after some time"), 
	SERVICE_UNAVAILABLE("10006", "Service Unavailable, please try again later");
	
	
	private String errorCode;
	private String errorMessage;
	
	ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	

}
