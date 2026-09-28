package com.cpt.payments.processing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTxnRequest {
    private String userId;
    private String paymentMethod;
    private String provider;
    private String paymentType;
    private double amount;
    private String currency;
    private String txnStatus;
    private String merchantTransactionReference;
}