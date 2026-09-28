package com.cpt.payments.dao;

import com.cpt.payments.constant.MerchantReqUpdate;

public interface MerchantPaymentRequestDao {

	public MerchantReqUpdate insertMerchantPaymentRequest(String endUserID, 
            String merchantTransactionReference, 
            String transactionRequest);
	
	public int getUserPaymentAttemptsInLastXMinutes(String endUserId, int durationInMins);
	
}
