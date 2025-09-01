package com.barclays.eaglebank.model.requests;

import com.barclays.eaglebank.enums.AccountType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateBankAccountRequestTest {

    @Test
    void builder_CreatesValidRequest() {
        CreateBankAccountRequest request = CreateBankAccountRequest.builder()
                .name("Main Account")
                .accountType(AccountType.PERSONAL)
                .build();

        assertThat(request).isNotNull();
        assertThat(request.getName()).isEqualTo("Main Account");
        assertThat(request.getAccountType()).isEqualTo(AccountType.PERSONAL);
    }
}
