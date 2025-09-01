package com.barclays.eaglebank.service.transaction;

import com.barclays.eaglebank.enums.TransactionType;
import com.barclays.eaglebank.exceptions.NotFoundException;
import com.barclays.eaglebank.exceptions.UnprocessableException;
import com.barclays.eaglebank.model.requests.CreateTransactionRequest;
import com.barclays.eaglebank.model.requests.UpdateBankAccountRequest;
import com.barclays.eaglebank.model.response.BankAccountResponse;
import com.barclays.eaglebank.model.response.TransactionResponse;
import com.barclays.eaglebank.repository.TransactionRepository;
import com.barclays.eaglebank.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

import static com.barclays.eaglebank.enums.TransactionType.*;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionResponse createTransaction(String accountNumber, String userId, CreateTransactionRequest request) {
        BankAccountResponse account = accountService.fetchAccountForUser(accountNumber, userId);

        double amount = request.getAmount();
        TransactionType type = request.getType();

        if (type == WITHDRAWAL && account.getBalance() < amount) {
            throw new UnprocessableException("Insufficient funds");
        }

        double newBalance = calculateNewBalance(account.getBalance(), amount, type);

        accountService.updateAccountForUser(accountNumber, userId,
                UpdateBankAccountRequest.builder()
                        .name(account.getName())
                        .accountType(account.getAccountType())
                        .build());

        TransactionResponse newTransaction = TransactionResponse.builder()
                .id(generateUniqueTransactionNumber())
                .amount(amount)
                .currency(request.getCurrency())
                .type(type)
                .reference(request.getReference())
                .userId(userId)
                .createdTimestamp(OffsetDateTime.now())
                .build();

        return transactionRepository.save(accountNumber, newTransaction);
    }

    @Override
    public List<TransactionResponse> listTransactions(String accountNumber, String userId) {
        accountService.fetchAccountForUser(accountNumber, userId);
        return transactionRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public TransactionResponse fetchTransaction(String accountNumber, String transactionId, String userId) {
        accountService.fetchAccountForUser(accountNumber, userId);

        TransactionResponse transaction = transactionRepository.findById(accountNumber, transactionId);
        if (transaction == null) {
            throw new NotFoundException("Transaction not found");
        }

        return transaction;
    }

    private double calculateNewBalance(double current, double amount, TransactionType type) {
        double updated = current;

        if (type == DEPOSIT || type == CREDIT) {
            updated += amount;
        } else if (type == WITHDRAWAL || type == DEBIT) {
            updated -= amount;
        }

        return Math.round(updated * 100.0) / 100.0;
    }

    private String generateUniqueTransactionNumber() {
        return "tan-" + RandomStringUtils.randomNumeric(9);
    }
}
