package com.cpt.payments.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpRequest {

	private String url;
	private HttpMethod method;
	private HttpHeaders headers;
	private String requestBody;

}
