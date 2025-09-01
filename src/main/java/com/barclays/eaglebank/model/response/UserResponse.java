package com.barclays.eaglebank.model.response;

import com.barclays.eaglebank.model.Address;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;


@Builder(toBuilder = true)
@Data
public class UserResponse {

    private String id;
    
    private String name;
    
    private Address address;
    
    private String phoneNumber;
    
    private String email;
    
    private OffsetDateTime createdTimestamp;
    
    private OffsetDateTime updatedTimestamp;
}

