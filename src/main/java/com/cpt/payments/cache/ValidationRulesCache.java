package com.cpt.payments.cache;

import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.cpt.payments.dao.ValidationRuleDAO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ValidationRulesCache {

	private final RedisTemplate<String, String> redisTemplate;
	
	
	
	private final ListOperations<String, String> listOperations;
	private final HashOperations<String, String, String> hashOperations;

	private ValidationRuleDAO validationRuleDAO;

	private static final String VALIDATION_RULES_KEY = "validationRules";
	private static final String VALIDATION_RULES_PARAM_KEY_PREFIX = "validationRulesParams:";

	public ValidationRulesCache(RedisTemplate<String, String> redisTemplate, 
			ValidationRuleDAO validationRuleDAO) {
		this.redisTemplate = redisTemplate;
		this.listOperations = redisTemplate.opsForList();
		this.hashOperations = redisTemplate.opsForHash();
		this.validationRuleDAO = validationRuleDAO;
	}

	public void addValidationRulesList(List<String> rules) {
		listOperations.rightPushAll(VALIDATION_RULES_KEY, rules);
	}
	
	public void addValidationRuleParams(String validatorRuleName, 
			Map<String, String> params) {
		hashOperations.putAll(VALIDATION_RULES_PARAM_KEY_PREFIX + validatorRuleName, 
				params);
	}
	
	public void resetValidationRulesAndParams() {
        redisTemplate.delete(VALIDATION_RULES_KEY);
        
        redisTemplate.keys(VALIDATION_RULES_PARAM_KEY_PREFIX + "*")
            .forEach(key -> redisTemplate.delete(key));
	}
	
	public List<String> getValidationRulesList() {

		List<String> validationRules = listOperations.range(VALIDATION_RULES_KEY, 0, -1);

		if (validationRules != null && !validationRules.isEmpty()) {
			log.info("Loaded validation rules from cache: {}", validationRules);
			return validationRules;
		}

		log.info("Loading validation rules from DB");
		validationRules = validationRuleDAO.loadActiveValidatorNames();

		addValidationRulesList(validationRules);

		return validationRules;    
	}
	
	public Map<String, String> getValidationRulesParams(String validatorRuleName) {
		Map<String, String> validatorRuleParams = hashOperations.entries(
				VALIDATION_RULES_PARAM_KEY_PREFIX + validatorRuleName);
		
		if (!validatorRuleParams.isEmpty()) {
			return validatorRuleParams;
		}
		
		validatorRuleParams = validationRuleDAO.loadValidatorRuleParams(validatorRuleName);
		if (validatorRuleParams != null && !validatorRuleParams.isEmpty()) {
			addValidationRuleParams(validatorRuleName, validatorRuleParams);
		}
		
		return validatorRuleParams;
	}
	
	public void loadValidatorRulesAndParams() {
		List<String> validationRulesList = getValidationRulesList();
		log.info("Loaded validation rules: {}", validationRulesList);
		
		validationRulesList.forEach(  
				rule -> {
					Map<String, String> params = getValidationRulesParams(rule);
					log.info("Loaded rule: {} | params: {}", rule, params);
				});
	}



}
