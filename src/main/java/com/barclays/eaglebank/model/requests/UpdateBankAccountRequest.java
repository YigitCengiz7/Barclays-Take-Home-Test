package com.barclays.eaglebank.model.requests;

import com.barclays.eaglebank.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBankAccountRequest {
    @NotBlank(message = "name must not be blank")
    private String name;
    private AccountType accountType;
}