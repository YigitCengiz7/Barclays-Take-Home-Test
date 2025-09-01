package com.barclays.eaglebank.service.account;

import com.barclays.eaglebank.model.requests.CreateBankAccountRequest;
import com.barclays.eaglebank.model.requests.UpdateBankAccountRequest;
import com.barclays.eaglebank.model.response.BankAccountResponse;

import java.util.List;

public interface AccountService {
    BankAccountResponse createAccount(String userId, CreateBankAccountRequest request);
    List<BankAccountResponse> listBankAccounts(String userId);
    BankAccountResponse fetchAccountForUser(String accountNumber, String userId);
    BankAccountResponse updateAccountForUser(String accountNumber, String userId, UpdateBankAccountRequest request);
    void deleteAccountForUser(String accountNumber, String userId);
    boolean userHasAnyAccount(String userId);
}