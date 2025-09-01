package com.barclays.eaglebank.controller;

import com.barclays.eaglebank.enums.SortCode;
import com.barclays.eaglebank.exceptions.GlobalControllerAdvice;
import com.barclays.eaglebank.model.requests.CreateBankAccountRequest;
import com.barclays.eaglebank.model.requests.CreateTransactionRequest;
import com.barclays.eaglebank.model.requests.UpdateBankAccountRequest;
import com.barclays.eaglebank.model.response.BankAccountResponse;
import com.barclays.eaglebank.model.response.TransactionResponse;
import com.barclays.eaglebank.service.account.AccountService;
import com.barclays.eaglebank.service.transaction.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;

import static com.barclays.eaglebank.enums.AccountType.PERSONAL;
import static com.barclays.eaglebank.enums.Currency.GBP;
import static com.barclays.eaglebank.enums.TransactionType.DEPOSIT;
import static com.barclays.eaglebank.enums.TransactionType.WITHDRAWAL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock private AccountService accountService;
    @Mock private TransactionService transactionService;

    private MockMvc mockMvc;
    private ObjectMapper om;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AccountController(accountService, transactionService))
                .setControllerAdvice(new GlobalControllerAdvice())
                .build();
        om = new ObjectMapper();
    }

    @Test
    void createAccount_returns201() throws Exception {
        var req = CreateBankAccountRequest.builder()
                .name("Main Account")
                .accountType(PERSONAL)
                .build();

        var resp = BankAccountResponse.builder()
                .accountNumber("01234567")
                .name("Main Account")
                .accountType(PERSONAL)
                .sortCode(SortCode.DEFAULT)
                .balance(0.0)
                .currency(GBP)
                .createdTimestamp(OffsetDateTime.now())
                .updatedTimestamp(OffsetDateTime.now())
                .build();

        when(accountService.createAccount(any(), any())).thenReturn(resp);

        mockMvc.perform(post("/v1/accounts")
                        .requestAttr("authUserId", "usr-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void listAccounts_returns200() throws Exception {
        var a = BankAccountResponse.builder().accountNumber("01234567").name("A").build();
        var b = BankAccountResponse.builder().accountNumber("01888888").name("B").build();
        when(accountService.listBankAccounts("usr-123")).thenReturn(List.of(a, b));

        System.out.println(List.of(a,b));
        mockMvc.perform(get("/v1/accounts")
                        .requestAttr("authUserId", "usr-123"))
                .andExpect(status().isOk());
    }

    @Test
    void fetchAccount_returns200() throws Exception {
        var acc = BankAccountResponse.builder()
                .accountNumber("01234567")
                .name("Main")
                .balance(1000.0)
                .currency(GBP)
                .build();

        when(accountService.fetchAccountForUser("01234567", "usr-123")).thenReturn(acc);

        mockMvc.perform(get("/v1/accounts/01234567")
                        .requestAttr("authUserId", "usr-123"))
                .andExpect(status().isOk());
    }

    @Test
    void updateAccount_returns200() throws Exception {
        var req = UpdateBankAccountRequest.builder()
                .name("Renamed")
                .accountType(PERSONAL)
                .build();

        var updated = BankAccountResponse.builder()
                .accountNumber("01234567")
                .name("Renamed")
                .accountType(PERSONAL)
                .build();

        when(accountService.updateAccountForUser("01234567", "usr-123", req)).thenReturn(updated);

        mockMvc.perform(patch("/v1/accounts/01234567")
                        .requestAttr("authUserId", "usr-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAccount_returns204() throws Exception {
        doNothing().when(accountService).deleteAccountForUser("01234567", "usr-123");

        mockMvc.perform(delete("/v1/accounts/01234567")
                        .requestAttr("authUserId", "usr-123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createTransaction_returns201() throws Exception {
        var req = CreateTransactionRequest.builder()
                .amount(100.0)
                .currency(GBP)
                .type(DEPOSIT)
                .reference("test")
                .build();

        var tx = TransactionResponse.builder()
                .id("tan-123")
                .amount(100.0)
                .currency(GBP)
                .type(DEPOSIT)
                .build();

        when(transactionService.createTransaction("01234567", "usr-123", req)).thenReturn(tx);

        mockMvc.perform(post("/v1/accounts/01234567/transactions")
                        .requestAttr("authUserId", "usr-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void listTransactions_returns200() throws Exception {
        var t1 = TransactionResponse.builder().id("tan-1").amount(10.0).type(DEPOSIT).build();
        var t2 = TransactionResponse.builder().id("tan-2").amount(5.0).type(WITHDRAWAL).build();
        when(transactionService.listTransactions("01234567", "usr-123")).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/v1/accounts/01234567/transactions")
                        .requestAttr("authUserId", "usr-123"))
                .andExpect(status().isOk());
    }

    @Test
    void fetchTransaction_returns200() throws Exception {
        var tx = TransactionResponse.builder().id("tan-123").amount(10.0).type(DEPOSIT).build();
        when(transactionService.fetchTransaction("01234567", "tan-123", "usr-123")).thenReturn(tx);

        mockMvc.perform(get("/v1/accounts/01234567/transactions/tan-123")
                        .requestAttr("authUserId", "usr-123"))
                .andExpect(status().isOk());
    }
}