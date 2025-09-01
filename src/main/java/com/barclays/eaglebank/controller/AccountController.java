package com.barclays.eaglebank.controller;

import com.barclays.eaglebank.model.requests.CreateBankAccountRequest;
import com.barclays.eaglebank.model.requests.CreateTransactionRequest;
import com.barclays.eaglebank.model.requests.UpdateBankAccountRequest;
import com.barclays.eaglebank.model.response.BankAccountResponse;
import com.barclays.eaglebank.model.response.TransactionResponse;
import com.barclays.eaglebank.service.account.AccountService;
import com.barclays.eaglebank.service.transaction.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/accounts")
@Validated
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(
            @RequestAttribute("authUserId") String userId,
            @Valid @RequestBody CreateBankAccountRequest body
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(userId, body));
    }

    @GetMapping
    public ResponseEntity<List<BankAccountResponse>> listBankAccounts(
            @RequestAttribute("authUserId") String userId
    ) {
        return ResponseEntity.ok(accountService.listBankAccounts(userId));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> fetchBankAccountDetails(
            @RequestAttribute("authUserId") String userId,
            @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber
    ) {
        return ResponseEntity.ok(accountService.fetchAccountForUser(accountNumber, userId));
    }

    @PatchMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> updateAccount(
            @RequestAttribute("authUserId") String userId,
            @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber,
            @Valid @RequestBody UpdateBankAccountRequest body
    ) {
        return ResponseEntity.ok(accountService.updateAccountForUser(accountNumber, userId, body));
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(
            @RequestAttribute("authUserId") String userId,
            @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber
    ) {
        accountService.deleteAccountForUser(accountNumber, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{accountNumber}/transactions")
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestAttribute("authUserId") String userId,
            @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber,
            @Valid @RequestBody CreateTransactionRequest body
    ) {
        TransactionResponse tx = transactionService.createTransaction(accountNumber, userId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(tx);
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionResponse>> listTransactions(
            @RequestAttribute("authUserId") String userId,
            @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber
    ) {
        return ResponseEntity.ok(transactionService.listTransactions(accountNumber, userId));
    }

    @GetMapping("/{accountNumber}/transactions/{transactionId}")
    public ResponseEntity<TransactionResponse> fetchTransaction(
            @RequestAttribute("authUserId") String userId,
            @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber,
            @PathVariable @Pattern(regexp = "^tan-[A-Za-z0-9]+$") String transactionId
    ) {
        return ResponseEntity.ok(transactionService.fetchTransaction(accountNumber, transactionId, userId));
    }
}