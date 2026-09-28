package com.cpt.payments.pojo;

import lombok.Data;

@Data
public class Payment {
    private String currency;
    private Double amount;
    private String brandName;
    private String locale;
    private String returnUrl;
    private String cancelUrl;
    private String country;
    private String merchantTxnRef;
    private String paymentMethod;
    private String providerId;
    private String paymentType;
}