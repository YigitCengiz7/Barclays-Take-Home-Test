package com.barclays.eaglebank.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransactionType {
    @JsonProperty("credit")     CREDIT,
    @JsonProperty("debit")      DEBIT,
    @JsonProperty("deposit")    DEPOSIT,
    @JsonProperty("withdrawal") WITHDRAWAL
}