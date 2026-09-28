package com.cpt.payments.service.impl.validators;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cpt.payments.constant.ErrorCodeEnum;
import com.cpt.payments.constant.MerchantReqUpdate;
import com.cpt.payments.dao.MerchantPaymentRequestDao;
import com.cpt.payments.dto.PaymentRequestDTO;
import com.cpt.payments.exception.ValidationException;
import com.cpt.payments.service.interfaces.Validator;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DuplicateTxnCheck implements Validator {

	private MerchantPaymentRequestDao merchantPaymentRequestDao;
	
	private Gson gson;
	
	public DuplicateTxnCheck(MerchantPaymentRequestDao merchantPaymentRequestDao, 
			Gson gson) {
		this.merchantPaymentRequestDao = merchantPaymentRequestDao;
		this.gson = gson;
	}
	
	@Override
	public void validate(PaymentRequestDTO paymentRequestDTO) {
		log.info("Validating payment request: {}", paymentRequestDTO);
		
		if (paymentRequestDTO.getPayment().getMerchantTxnRef() == null
				|| paymentRequestDTO.getPayment().getMerchantTxnRef().trim().isEmpty()) {
			log.error("Merchant transaction reference is null or empty");
			
			throw new ValidationException(
					ErrorCodeEnum.MERCHANT_TXN_REF_EMPTY.getErrorCode(),
					ErrorCodeEnum.MERCHANT_TXN_REF_EMPTY.getErrorMessage(), 
					HttpStatus.BAD_REQUEST);
		}
		
		paymentRequestDTO.getPayment().setMerchantTxnRef(
				paymentRequestDTO.getPayment().getMerchantTxnRef().trim());
		
		MerchantReqUpdate insertedRows = merchantPaymentRequestDao.insertMerchantPaymentRequest(
				paymentRequestDTO.getUser().getEndUserID(), 
				paymentRequestDTO.getPayment().getMerchantTxnRef(), 
				gson.toJson(paymentRequestDTO));
		
		if (insertedRows == MerchantReqUpdate.DUPLICATE) {//duplicate entry
			log.error("Duplicate entry for merchant payment request");
			
			throw new ValidationException(
					ErrorCodeEnum.DUPLICATE_MERCHANT_TXN_REF.getErrorCode(), 
					ErrorCodeEnum.DUPLICATE_MERCHANT_TXN_REF.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		if (insertedRows == MerchantReqUpdate.ERROR) {
			log.error("Error occurred while inserting merchant payment request");
			
			throw new ValidationException(
					ErrorCodeEnum.PAYMENT_NOT_SAVED.getErrorCode(), 
					ErrorCodeEnum.PAYMENT_NOT_SAVED.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		log.info("DuplicateTxnCheck PASSED SUCCESS merchantTxnRef:{}", 
				paymentRequestDTO.getPayment().getMerchantTxnRef());
	}

}
