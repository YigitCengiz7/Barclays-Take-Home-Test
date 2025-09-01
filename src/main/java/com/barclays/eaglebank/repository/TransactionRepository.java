package com.barclays.eaglebank.repository;

import com.barclays.eaglebank.model.response.TransactionResponse;

import java.util.List;

public interface TransactionRepository {
    TransactionResponse save(String accountNumber, TransactionResponse transaction);
    List<TransactionResponse> findByAccountNumber(String accountNumber);
    TransactionResponse findById(String accountNumber, String transactionId);
    void deleteByAccountNumber(String accountNumber);
}