package com.barclays.eaglebank.service;

import com.barclays.eaglebank.exceptions.ConflictException;
import com.barclays.eaglebank.exceptions.NotFoundException;
import com.barclays.eaglebank.model.Address;
import com.barclays.eaglebank.model.requests.CreateUserRequest;
import com.barclays.eaglebank.model.requests.UpdateUserRequest;
import com.barclays.eaglebank.model.response.UserResponse;
import com.barclays.eaglebank.repository.UserRepository;
import com.barclays.eaglebank.service.account.AccountService;
import com.barclays.eaglebank.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountService accountService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, accountService);
    }

    @Test
    void create_ValidRequest_ReturnsUserResponse() {
        Address address = Address.builder()
                .line1("123 Main St")
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

        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(userRepository.save(any(UserResponse.class))).thenReturn(UserResponse.builder().build());

        UserResponse result = userService.create(request);

        assertThat(result).isNotNull();
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository).save(any(UserResponse.class));
    }

    @Test
    void create_EmailAlreadyExists_ThrowsConflictException() {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("existing@example.com")
                .build();

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(request));
        verify(userRepository, never()).save(any(UserResponse.class));
    }

    @Test
    void findByEmail_ValidEmail_ReturnsUser() {
        UserResponse expectedUser = UserResponse.builder().build();
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(expectedUser));

        Optional<UserResponse> result = userService.findByEmail("john.doe@example.com");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedUser);
    }

    @Test
    void findById_ValidId_ReturnsUser() {
        UserResponse expectedUser = UserResponse.builder().build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(expectedUser));

        Optional<UserResponse> result = userService.findById("user123");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedUser);
    }

    @Test
    void update_ValidRequest_ReturnsUpdatedUser() {
        UserResponse existingUser = UserResponse.builder()
                .id("user123")
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("Jane Smith")
                .phoneNumber("+44 20 8765 4321")
                .build();

        when(userRepository.findById("user123")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(UserResponse.class))).thenReturn(existingUser);

        UserResponse result = userService.update("user123", updateRequest);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(UserResponse.class));
    }

    @Test
    void update_UserNotFound_ThrowsNotFoundException() {
        UpdateUserRequest updateRequest = UpdateUserRequest.builder().build();
        when(userRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update("invalid", updateRequest));
        verify(userRepository, never()).save(any(UserResponse.class));
    }

    @Test
    void delete_ValidId_DeletesUser() {
        UserResponse user = UserResponse.builder()
                .id("user123")
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(accountService.userHasAnyAccount("user123")).thenReturn(false);

        userService.delete("user123");

        verify(userRepository).deleteById("user123");
    }

    @Test
    void delete_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete("invalid"));
        verify(userRepository, never()).deleteById(anyString());
    }
}
