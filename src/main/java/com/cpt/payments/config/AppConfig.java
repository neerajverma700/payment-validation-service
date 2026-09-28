package com.cpt.payments.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

@Configuration
public class AppConfig {

	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	@Bean
	Gson gson() {
		return new Gson();
	}
	
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
