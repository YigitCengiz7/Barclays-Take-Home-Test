package com.barclays.eaglebank.repository;

import com.barclays.eaglebank.model.response.BankAccountResponse;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final Map<String, BankAccountResponse> accounts = new HashMap<>();
    private final Map<String, String> accountOwners = new HashMap<>();

    @Override
    public BankAccountResponse save(BankAccountResponse account) {
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    @Override
    public Optional<BankAccountResponse> findByAccountNumber(String accountNumber) {
        return Optional.ofNullable(accounts.get(accountNumber));
    }

    @Override
    public List<BankAccountResponse> findByUserId(String userId) {
        return accountOwners.entrySet().stream()
                .filter(entry -> entry.getValue().equals(userId))
                .map(entry -> accounts.get(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByAccountNumber(String accountNumber) {
        accounts.remove(accountNumber);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return accounts.containsKey(accountNumber);
    }

    @Override
    public void saveAccountOwner(String accountNumber, String userId) {
        accountOwners.put(accountNumber, userId);
    }

    @Override
    public Optional<String> findOwnerByAccountNumber(String accountNumber) {
        return Optional.ofNullable(accountOwners.get(accountNumber));
    }

    @Override
    public void deleteAccountOwner(String accountNumber) {
        accountOwners.remove(accountNumber);
    }
}
