package com.barclays.eaglebank.model.response;

import com.barclays.eaglebank.enums.Currency;
import com.barclays.eaglebank.enums.TransactionType;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String id;
    
    private double amount;
    
    private Currency currency;
    
    private TransactionType type;
    
    private String reference;
    
    private String userId;
    
    private OffsetDateTime createdTimestamp;
}
