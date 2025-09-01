package com.barclays.eaglebank.model.requests;

import com.barclays.eaglebank.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String name;

    @Valid
    private Address address;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,15}$", message = "must match phone number pattern")
    private String phoneNumber;

    @Email
    private String email;

}

