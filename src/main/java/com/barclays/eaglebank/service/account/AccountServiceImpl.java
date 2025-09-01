package com.barclays.eaglebank.service.account;

import com.barclays.eaglebank.enums.Currency;
import com.barclays.eaglebank.enums.SortCode;
import com.barclays.eaglebank.exceptions.ForbiddenException;
import com.barclays.eaglebank.exceptions.NotFoundException;
import com.barclays.eaglebank.model.requests.CreateBankAccountRequest;
import com.barclays.eaglebank.model.requests.UpdateBankAccountRequest;
import com.barclays.eaglebank.model.response.BankAccountResponse;
import com.barclays.eaglebank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public BankAccountResponse createAccount(String userId, CreateBankAccountRequest request) {
        String accountNumber = generateUniqueAccountNumber();

        BankAccountResponse newAccount = BankAccountResponse.builder()
                .accountNumber(accountNumber)
                .name(request.getName().trim())
                .accountType(request.getAccountType())
                .sortCode(SortCode.DEFAULT)
                .balance(0.00)
                .currency(Currency.GBP)
                .createdTimestamp(OffsetDateTime.now())
                .updatedTimestamp(OffsetDateTime.now())
                .build();

        accountRepository.save(newAccount);
        accountRepository.saveAccountOwner(accountNumber, userId);
        return newAccount;
    }

    @Override
    public List<BankAccountResponse> listBankAccounts(String userId) {
        return accountRepository.findByUserId(userId);
    }

    @Override
    public BankAccountResponse fetchAccountForUser(String accountNumber, String userId) {
        return getAccountIfOwned(accountNumber, userId);
    }

    @Override
    public BankAccountResponse updateAccountForUser(String accountNumber, String userId, UpdateBankAccountRequest request) {
        BankAccountResponse existing = getAccountIfOwned(accountNumber, userId);

        BankAccountResponse updated = BankAccountResponse.builder()
                .accountNumber(existing.getAccountNumber())
                .sortCode(existing.getSortCode())
                .name(request.getName() != null && !request.getName().trim().isEmpty()
                        ? request.getName().trim() : existing.getName())
                .accountType(request.getAccountType() != null
                        ? request.getAccountType() : existing.getAccountType())
                .balance(existing.getBalance())
                .currency(existing.getCurrency())
                .createdTimestamp(existing.getCreatedTimestamp())
                .updatedTimestamp(OffsetDateTime.now())
                .build();

        return accountRepository.save(updated);
    }

    @Override
    public void deleteAccountForUser(String accountNumber, String userId) {
        getAccountIfOwned(accountNumber, userId);
        accountRepository.deleteByAccountNumber(accountNumber);
        accountRepository.deleteAccountOwner(accountNumber);
    }

    @Override
    public boolean userHasAnyAccount(String userId) {
        return !accountRepository.findByUserId(userId).isEmpty();
    }

    private BankAccountResponse getAccountIfOwned(String accountNumber, String userId) {
        BankAccountResponse account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Bank account not found"));

        String owner = accountRepository.findOwnerByAccountNumber(accountNumber)
                .orElseThrow(() -> new ForbiddenException("Account ownership not found"));

        if (!userId.equals(owner)) throw new ForbiddenException("Access denied");
        return account;
    }

    private String generateUniqueAccountNumber() {
        return "01" + RandomStringUtils.randomNumeric(6);
    }
}