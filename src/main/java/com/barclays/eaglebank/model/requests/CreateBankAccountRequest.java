package com.barclays.eaglebank.model.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.barclays.eaglebank.enums.AccountType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBankAccountRequest {

    @NotBlank(message = "name must not be blank")
    private String name;

    @NotNull(message = "accountType is required")
    private AccountType accountType;
}
