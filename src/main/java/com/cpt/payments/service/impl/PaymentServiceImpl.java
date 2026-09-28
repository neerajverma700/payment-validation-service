package com.cpt.payments.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cpt.payments.cache.ValidationRulesCache;
import com.cpt.payments.constant.ValidatorEnum;
import com.cpt.payments.dto.PaymentRequestDTO;
import com.cpt.payments.dto.PaymentResponseDTO;
import com.cpt.payments.exception.ValidationException;
import com.cpt.payments.http.HttpRequest;
import com.cpt.payments.http.HttpServiceEngine;
import com.cpt.payments.processing.CreateTxnRequest;
import com.cpt.payments.processing.ProcessingPaymentError;
import com.cpt.payments.processing.TransactionRes;
import com.cpt.payments.service.interfaces.PaymentService;
import com.cpt.payments.service.interfaces.Validator;
import com.google.gson.Gson;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

	private ApplicationContext applicationContext;
	
	private ValidationRulesCache validationRulesCache;
	
	private HttpServiceEngine httpServiceEngine;
	
	private Gson gson;
	
	@Value("${processing.createpayment.url}")
	private String createPaymentUrl;
	
	public PaymentServiceImpl(ApplicationContext applicationContext, 
			ValidationRulesCache validationRulesCache,
			HttpServiceEngine httpServiceEngine,
			Gson gson) {
		this.applicationContext = applicationContext;
		this.validationRulesCache = validationRulesCache;
		this.httpServiceEngine = httpServiceEngine;
		this.gson = gson;
	}
	
	@Override
	public PaymentResponseDTO validateAndProcessPayment(PaymentRequestDTO paymentRequest) {
		
		//String name = null;
		//name.length();
		
		log.info("Payment request received: {}", paymentRequest);
		
		validationRulesCache.getValidationRulesList().forEach(
				rule -> triggerValidationRule(paymentRequest, rule));
		
		// return errorCode & errorMessage
		log.info("Payment request processed successfully. All rules passed");
		
		// TODO Invoke CreateTxn API of processing
		
		TransactionRes response = invokeCreatePayment(paymentRequest);
		
		// TODO Invoke InitiateTxn API of processing
		
		log.info("Payment request processed successfully. createPayment Success||"
				+ "response:{}", response);
		
		
		String txnId = "TX123";
		String redirectUrl = "http://www.google.com"; 
		
		
		PaymentResponseDTO paymentResponse = new PaymentResponseDTO(
				txnId, redirectUrl);
		
		log.info("Payment response: {}", paymentResponse);
		return paymentResponse;
	}

	private TransactionRes invokeCreatePayment(PaymentRequestDTO paymentRequest) {
		// Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Prepare the request body
        CreateTxnRequest req = CreateTxnRequest.builder()
        		.userId(paymentRequest.getUser().getEndUserID())
                .paymentMethod(paymentRequest.getPayment().getPaymentMethod())
                .provider(paymentRequest.getPayment().getProviderId())
                .paymentType(paymentRequest.getPayment().getPaymentType())
                .amount(paymentRequest.getPayment().getAmount())
                .currency(paymentRequest.getPayment().getCurrency())
                .txnStatus("CREATED") // TODO replace this hardcoded with Enum constant
                .merchantTransactionReference(paymentRequest.getPayment().getMerchantTxnRef())
				.build();
        
        log.info("Request to API:{}", req);
        
        String requestBody = gson.toJson(req);
		
		HttpRequest httpRequest = HttpRequest.builder()
				.url(createPaymentUrl)
				.method(HttpMethod.POST)
				.headers(headers)
				.requestBody(requestBody)
				.build();
		
		log.info("Making HTTP request to processing", httpRequest);
		
		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpRequest(httpRequest);
		log.info("HTTP Response from processing: {}", httpResponse);
		
		// httpResponse is success. convert received json to success java object.
		
		TransactionRes response = null;
		if(httpResponse.getStatusCode().isSameCodeAs(HttpStatus.CREATED)) {
			// means success response came. Now you can convert receive json to Success Object Structure.
			response = gson.fromJson(httpResponse.getBody(), TransactionRes.class);
			log.info("Response from processing: {}", response);
		} else {
			// handle error response
			log.error("Error response from processing: {}", httpResponse);
			
			ProcessingPaymentError errorResponse = gson.fromJson(httpResponse.getBody(), ProcessingPaymentError.class);
			log.error("Error response from processing: {}", errorResponse);
			
			throw new ValidationException(
					errorResponse.getErrorCode(), 
					errorResponse.getErrorMessage(), 
					HttpStatus.valueOf(httpResponse.getStatusCode().value()));
		}
		return response;
	}

	private String triggerValidationRule(PaymentRequestDTO paymentRequest, String rule) {
		rule = rule.trim();
		log.info("Validating payment request using rule: {}", rule);
		
		Validator validator = null;
		
		Class<? extends Validator> validatorClass = ValidatorEnum.getClassByName(rule);
		
		if (validatorClass != null) {
			validator = applicationContext.getBean(validatorClass);
			
			if (validator != null) {
				log.info("Calling validator rule rule: {}", rule);
				validator.validate(paymentRequest);
			}
		}
		
		if (validatorClass == null || validator == null) {
			log.error("Either Validator class not found or Validator instance not found for "
					+ "|rule:{} | validatorClass:{} | validator:{}", 
					rule, validatorClass, validator);
			//continue;
		}
		
		// RUle ran successfully
		return rule;
	}
	
	@PostConstruct
	public void loadValidatorRules() {
		validationRulesCache.loadValidatorRulesAndParams();
		log.info("Loaded validator rules from cache");
	}

	

}
