package com.barclays.eaglebank.repository;

import com.barclays.eaglebank.model.response.TransactionResponse;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final Map<String, List<TransactionResponse>> transactions = new HashMap<>();

    @Override
    public TransactionResponse save(String accountNumber, TransactionResponse transaction) {
        transactions.computeIfAbsent(accountNumber, k -> new ArrayList<>()).add(transaction);
        return transaction;
    }

    @Override
    public List<TransactionResponse> findByAccountNumber(String accountNumber) {
        return new ArrayList<>(transactions.getOrDefault(accountNumber, List.of()));
    }

    @Override
    public TransactionResponse findById(String accountNumber, String transactionId) {
        for (TransactionResponse tx : transactions.getOrDefault(accountNumber, List.of())) {
            if (tx.getId().equals(transactionId)) return tx;
        }
        return null;
    }

    @Override
    public void deleteByAccountNumber(String accountNumber) {
        transactions.remove(accountNumber);
    }
}