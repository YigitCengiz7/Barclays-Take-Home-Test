package com.barclays.eaglebank.repository;

import com.barclays.eaglebank.model.response.UserResponse;

import java.util.Optional;

public interface UserRepository {
    
    UserResponse save(UserResponse user);
    
    Optional<UserResponse> findById(String userId);
    
    Optional<UserResponse> findByEmail(String email);
    
    void deleteById(String userId);
    
    boolean existsByEmail(String email);
}
