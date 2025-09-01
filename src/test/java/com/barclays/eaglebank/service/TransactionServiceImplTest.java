package com.barclays.eaglebank.service;

import com.barclays.eaglebank.enums.Currency;
import com.barclays.eaglebank.enums.TransactionType;
import com.barclays.eaglebank.exceptions.NotFoundException;
import com.barclays.eaglebank.exceptions.UnprocessableException;
import com.barclays.eaglebank.model.requests.CreateTransactionRequest;
import com.barclays.eaglebank.model.response.BankAccountResponse;
import com.barclays.eaglebank.model.response.TransactionResponse;
import com.barclays.eaglebank.repository.TransactionRepository;
import com.barclays.eaglebank.service.account.AccountService;
import com.barclays.eaglebank.service.transaction.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionServiceImpl(accountService, transactionRepository);
    }

    @Test
    void createTransaction_ValidRequest_ReturnsTransactionResponse() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .amount(100.0)
                .currency(Currency.GBP)
                .type(TransactionType.DEPOSIT)
                .reference("Test deposit")
                .build();

        BankAccountResponse account = BankAccountResponse.builder()
                .accountNumber("ACC123")
                .balance(500.0)
                .currency(Currency.GBP)
                .build();

        when(accountService.fetchAccountForUser("ACC123", "user123")).thenReturn(account);
        when(accountService.updateAccountForUser(anyString(), anyString(), any())).thenReturn(account);
        when(transactionRepository.save(anyString(), any(TransactionResponse.class))).thenReturn(new TransactionResponse());

        TransactionResponse result = transactionService.createTransaction("ACC123", "user123", request);

        assertThat(result).isNotNull();
        verify(transactionRepository).save(anyString(), any(TransactionResponse.class));
        verify(accountService).updateAccountForUser(anyString(), anyString(), any());
    }

    @Test
    void createTransaction_AccountNotFound_ThrowsNotFoundException() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .amount(100.0)
                .currency(Currency.GBP)
                .type(TransactionType.DEPOSIT)
                .build();

        when(accountService.fetchAccountForUser("INVALID", "user123"))
                .thenThrow(new NotFoundException("Account not found"));

        assertThrows(NotFoundException.class, () -> transactionService.createTransaction("INVALID", "user123", request));
    }

    @Test
    void createTransaction_InsufficientFunds_ThrowsUnprocessableException() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .amount(1000.0)
                .currency(Currency.GBP)
                .type(TransactionType.WITHDRAWAL)
                .build();

        BankAccountResponse account = BankAccountResponse.builder()
                .accountNumber("ACC123")
                .balance(100.0)
                .build();

        when(accountService.fetchAccountForUser("ACC123", "user123")).thenReturn(account);

        assertThrows(UnprocessableException.class, () -> transactionService.createTransaction("ACC123", "user123", request));
    }

    @Test
    void listTransactions_ValidAccount_ReturnsTransactionList() {
        List<TransactionResponse> expectedTransactions = List.of(
                new TransactionResponse(), new TransactionResponse()
        );
        when(transactionRepository.findByAccountNumber("ACC123")).thenReturn(expectedTransactions);
        when(accountService.fetchAccountForUser("ACC123", "user123")).thenReturn(new BankAccountResponse());

        List<TransactionResponse> result = transactionService.listTransactions("ACC123", "user123");

        assertThat(result).isEqualTo(expectedTransactions);
        verify(transactionRepository).findByAccountNumber("ACC123");
    }

    @Test
    void fetchTransaction_ValidId_ReturnsTransaction() {
        TransactionResponse expectedTransaction = new TransactionResponse();
        when(transactionRepository.findById("ACC123", "TXN123")).thenReturn(expectedTransaction);
        when(accountService.fetchAccountForUser("ACC123", "user123")).thenReturn(new BankAccountResponse());

        TransactionResponse result = transactionService.fetchTransaction("ACC123", "TXN123", "user123");

        assertThat(result).isEqualTo(expectedTransaction);
        verify(transactionRepository).findById("ACC123", "TXN123");
    }
}
