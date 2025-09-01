package com.barclays.eaglebank.service.user;

import com.barclays.eaglebank.model.requests.CreateUserRequest;
import com.barclays.eaglebank.model.requests.UpdateUserRequest;
import com.barclays.eaglebank.model.response.UserResponse;
import com.barclays.eaglebank.exceptions.ConflictException;
import com.barclays.eaglebank.exceptions.NotFoundException;
import com.barclays.eaglebank.repository.UserRepository;
import com.barclays.eaglebank.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;


    @Override
    public UserResponse create(CreateUserRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("User with this email already exists");
        }

        String userId = "usr-" + System.currentTimeMillis();
        OffsetDateTime now = OffsetDateTime.now();

        UserResponse newUser = UserResponse.builder()
                .id(userId)
                .name(request.getName().trim())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .email(email)
                .createdTimestamp(now)
                .updatedTimestamp(now)
                .build();

        return userRepository.save(newUser);
    }

    @Override
    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserResponse update(String userId, UpdateUserRequest request) {
        UserResponse currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String newName = currentUser.getName();
        String newPhoneNumber = currentUser.getPhoneNumber();
        String newEmail = currentUser.getEmail();

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            newName = request.getName().trim();
        }

        if (request.getPhoneNumber() != null) {
            newPhoneNumber = request.getPhoneNumber();
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String cleanNewEmail = request.getEmail().trim().toLowerCase();


            if (!currentUser.getEmail().equals(cleanNewEmail) && userRepository.existsByEmail(cleanNewEmail)) {
                throw new ConflictException("User with this email already exists");
            }

            newEmail = cleanNewEmail;
        }

        UserResponse updatedUser = UserResponse.builder()
                .id(currentUser.getId())
                .name(newName)
                .address(request.getAddress() != null ? request.getAddress() : currentUser.getAddress())
                .phoneNumber(newPhoneNumber)
                .email(newEmail)
                .createdTimestamp(currentUser.getCreatedTimestamp())
                .updatedTimestamp(OffsetDateTime.now())
                .build();

        return userRepository.save(updatedUser);
    }

    @Override
    public void delete(String userId) {
        var existing = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User was not found"));

        if (accountService.userHasAnyAccount(userId)) {
            throw new ConflictException("A user cannot be deleted when they are associated with a bank account");
        }

        userRepository.deleteById(existing.getId());
    }

    @Override
    public Optional<UserResponse> findById(String userId) {
        return userRepository.findById(userId);
    }


}
