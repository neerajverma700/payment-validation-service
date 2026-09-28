package com.cpt.payments.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpt.payments.service.interfaces.ValidatorRuleService;

@RestController
@RequestMapping("/v1/validation-rules")
public class ValidationRulesController {

	private ValidatorRuleService validatorRuleService;

	public ValidationRulesController(ValidatorRuleService validatorRuleService) {
		this.validatorRuleService = validatorRuleService;
	}

	@PostMapping("/reload")
	public ResponseEntity<String> reloadValidationRulesAndParams() {

		validatorRuleService.reloadValidationRulesAndParams();

		return ResponseEntity.ok("Validation rules and params reloaded successfully");
	}
}
