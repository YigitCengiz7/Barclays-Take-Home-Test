package com.barclays.eaglebank.model.requests;

import com.barclays.eaglebank.model.Address;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserRequestTest {

    @Test
    void builder_CreatesValidRequest() {
        Address address = Address.builder()
                .line1("123 Main Street")
                .town("London")
                .county("Greater London")
                .postcode("SW1A 1AA")
                .build();

        CreateUserRequest request = CreateUserRequest.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .address(address)
                .phoneNumber("+44 20 1234 5678")
                .build();

        assertThat(request).isNotNull();
        assertThat(request.getName()).isEqualTo("John Doe");
        assertThat(request.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(request.getAddress()).isEqualTo(address);
        assertThat(request.getPhoneNumber()).isEqualTo("+44 20 1234 5678");
    }
}
