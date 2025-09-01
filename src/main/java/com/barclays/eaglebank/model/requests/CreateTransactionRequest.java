package com.barclays.eaglebank.model.requests;

import com.barclays.eaglebank.enums.Currency;
import com.barclays.eaglebank.enums.TransactionType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {

    @NotNull
    @DecimalMin(value = "0.01", message = "must be > 0.00")
    @DecimalMax(value = "10000.00", message = "must be <= 10000.00")
    private Double amount;

    @NotNull
    private Currency currency;

    @NotNull
    private TransactionType type;

    private String reference;
}