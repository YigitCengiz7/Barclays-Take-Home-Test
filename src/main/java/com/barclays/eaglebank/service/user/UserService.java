package com.barclays.eaglebank.service.user;

import com.barclays.eaglebank.model.requests.CreateUserRequest;
import com.barclays.eaglebank.model.requests.UpdateUserRequest;
import com.barclays.eaglebank.model.response.UserResponse;

import java.util.Optional;

public interface UserService {
    UserResponse create(CreateUserRequest createUserRequest);

    Optional<UserResponse> findByEmail(String emailLowercase);

    Optional<UserResponse> findById(String userId);

    UserResponse update(String userId, UpdateUserRequest req);

    void delete(String userId);
}

