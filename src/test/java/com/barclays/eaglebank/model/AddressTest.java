package com.barclays.eaglebank.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

    @Test
    void builder_CreatesValidAddress() {
        Address address = Address.builder()
                .line1("456 Oak Avenue")
                .town("Manchester")
                .county("Greater Manchester")
                .postcode("M1 1AA")
                .build();

        assertThat(address).isNotNull();
        assertThat(address.getLine1()).isEqualTo("456 Oak Avenue");
        assertThat(address.getLine2()).isNull();
        assertThat(address.getLine3()).isNull();
        assertThat(address.getTown()).isEqualTo("Manchester");
        assertThat(address.getCounty()).isEqualTo("Greater Manchester");
        assertThat(address.getPostcode()).isEqualTo("M1 1AA");
    }
}
