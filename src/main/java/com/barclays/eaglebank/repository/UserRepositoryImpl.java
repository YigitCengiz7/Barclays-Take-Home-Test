package com.barclays.eaglebank.repository;

import com.barclays.eaglebank.model.response.UserResponse;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<String, UserResponse> usersById = new HashMap<>();
    private final Map<String, UserResponse> usersByEmail = new HashMap<>();

    @Override
    public UserResponse save(UserResponse user) {
        usersById.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public Optional<UserResponse> findById(String userId) {
        return Optional.ofNullable(usersById.get(userId));
    }

    @Override
    public Optional<UserResponse> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        String cleanEmail = email.trim().toLowerCase();
        return Optional.ofNullable(usersByEmail.get(cleanEmail));
    }

    @Override
    public void deleteById(String userId) {
        UserResponse user = usersById.get(userId);
        if (user != null) {
            usersById.remove(userId);
            usersByEmail.remove(user.getEmail());
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) {
            return false;
        }
        String cleanEmail = email.trim().toLowerCase();
        return usersByEmail.containsKey(cleanEmail);
    }
}
