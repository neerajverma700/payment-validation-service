package com.cpt.payments.http;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.cpt.payments.constant.ErrorCodeEnum;
import com.cpt.payments.exception.ValidationException;
import com.google.gson.Gson;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpServiceEngine {
	
	private RestTemplate restTemplate;
	
	private Gson gson;
	
	public HttpServiceEngine(RestTemplate restTemplate, Gson gson) {
		this.restTemplate = restTemplate;
		this.gson = gson;
	}
	
	@CircuitBreaker(name = "payment-processing-service", 
			fallbackMethod = "fallbackProcessPayment")
	public ResponseEntity<String> makeHttpRequest(HttpRequest httpRequest) {
		try {
			log.info("Making HTTP request:{}", httpRequest);
            
			// Create the request entity
            HttpEntity<String> requestEntity = new HttpEntity<>(
            		httpRequest.getRequestBody(), httpRequest.getHeaders());

            // Make the API call
            ResponseEntity<String> responseEntity = restTemplate.exchange(
            		httpRequest.getUrl(), 
            		httpRequest.getMethod(), 
            		requestEntity, 
            		String.class);

            // Return the response body
            String responseBody = responseEntity.getBody();
            log.info("Response from API:{}", responseBody);
            
			return responseEntity;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Handle HTTP errors
            log.error("Error while making HTTP request", e);
            
            if(e.getStatusCode().isSameCodeAs(HttpStatus.SERVICE_UNAVAILABLE)) {
            	log.error("503 SERVICE_UNAVAILABLE Fail|| Error while making HTTP request", e);
    			throw new ValidationException(
    					ErrorCodeEnum.SERVICE_UNAVAILABLE.getErrorCode(), 
    					ErrorCodeEnum.SERVICE_UNAVAILABLE.getErrorMessage(),
    					HttpStatus.SERVICE_UNAVAILABLE);
            }
            
            return ResponseEntity.status(e.getStatusCode())
            		.body(e.getResponseBodyAsString());
        } catch (Exception e) {
            // Handle exceptions (optional)
            log.error("Exception Block|| Error while making HTTP request", e);
            
			throw new ValidationException(
					ErrorCodeEnum.SERVICE_UNAVAILABLE.getErrorCode(), 
					ErrorCodeEnum.SERVICE_UNAVAILABLE.getErrorMessage(),
					HttpStatus.SERVICE_UNAVAILABLE);
        }
	}
	
	public ResponseEntity<String> fallbackProcessPayment(HttpRequest httpRequest, Throwable t) {
		log.error("Fallback method called|| Error while making HTTP request", t);
		
		throw new ValidationException(
				ErrorCodeEnum.SERVICE_UNAVAILABLE.getErrorCode(), 
				ErrorCodeEnum.SERVICE_UNAVAILABLE.getErrorMessage(),
				HttpStatus.SERVICE_UNAVAILABLE);
	}

	
}
