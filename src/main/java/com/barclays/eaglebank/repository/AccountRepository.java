package com.barclays.eaglebank.repository;

import com.barclays.eaglebank.model.response.BankAccountResponse;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    
    BankAccountResponse save(BankAccountResponse account);
    
    Optional<BankAccountResponse> findByAccountNumber(String accountNumber);
    
    List<BankAccountResponse> findByUserId(String userId);
    
    void deleteByAccountNumber(String accountNumber);
    
    boolean existsByAccountNumber(String accountNumber);
    
    void saveAccountOwner(String accountNumber, String userId);
    
    Optional<String> findOwnerByAccountNumber(String accountNumber);
    
    void deleteAccountOwner(String accountNumber);
}
