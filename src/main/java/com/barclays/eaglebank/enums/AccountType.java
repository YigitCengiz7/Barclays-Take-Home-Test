package com.barclays.eaglebank.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType {
    @JsonProperty("personal")
    PERSONAL;

    @JsonValue
    public String getValue() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static AccountType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        for (AccountType type : AccountType.values()) {
            if (type.name().equalsIgnoreCase(value) || 
                type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Invalid AccountType: " + value);
    }
}