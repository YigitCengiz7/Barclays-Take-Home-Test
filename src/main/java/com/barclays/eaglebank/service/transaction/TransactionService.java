package com.barclays.eaglebank.service.transaction;

import com.barclays.eaglebank.model.requests.CreateTransactionRequest;
import com.barclays.eaglebank.model.response.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(String accountNumber, String userId, CreateTransactionRequest request);
    List<TransactionResponse> listTransactions(String accountNumber, String userId);
    TransactionResponse fetchTransaction(String accountNumber, String transactionId, String userId);
}