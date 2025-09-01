package com.barclays.eaglebank.model.requests;

import com.barclays.eaglebank.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank
    private String name;

    @Valid
    @NotNull
    private Address address;

    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
    private String phoneNumber;

    @Email
    @NotBlank
    private String email;
}
