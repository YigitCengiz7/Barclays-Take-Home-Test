package com.barclays.eaglebank.enums;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SortCode {
    DEFAULT("10-10-10");

    private final String value;

    SortCode(String value) { this.value = value; }

    @JsonValue
    public String value() { return value; }
}