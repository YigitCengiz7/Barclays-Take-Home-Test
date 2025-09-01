package com.barclays.eaglebank.model.response;

import com.barclays.eaglebank.enums.AccountType;
import com.barclays.eaglebank.enums.Currency;
import com.barclays.eaglebank.enums.SortCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponse {
    private String accountNumber;
    
    private SortCode sortCode;
    
    private String name;
    
    private AccountType accountType;
    
    private double balance;
    
    private Currency currency;
    
    private OffsetDateTime createdTimestamp;
    
    private OffsetDateTime updatedTimestamp;
}
