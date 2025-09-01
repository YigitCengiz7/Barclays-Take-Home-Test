package com.barclays.eaglebank.service;

import com.barclays.eaglebank.enums.AccountType;
import com.barclays.eaglebank.enums.Currency;
import com.barclays.eaglebank.enums.SortCode;
import com.barclays.eaglebank.exceptions.ForbiddenException;
import com.barclays.eaglebank.exceptions.NotFoundException;
import com.barclays.eaglebank.model.requests.CreateBankAccountRequest;
import com.barclays.eaglebank.model.requests.UpdateBankAccountRequest;
import com.barclays.eaglebank.model.response.BankAccountResponse;
import com.barclays.eaglebank.repository.AccountRepository;
import com.barclays.eaglebank.service.account.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    AccountRepository repo;
    @InjectMocks
    AccountServiceImpl service;

    private BankAccountResponse existing() {
        return BankAccountResponse.builder()
                .accountNumber("012345")
                .name("Existing")
                .accountType(AccountType.PERSONAL)
                .sortCode(SortCode.DEFAULT)
                .balance(100.00)
                .currency(Currency.GBP)
                .createdTimestamp(OffsetDateTime.now().minusDays(1))
                .updatedTimestamp(OffsetDateTime.now().minusDays(1))
                .build();
    }

    @Test
    void createAccount_createsAndLinksOwner() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = CreateBankAccountRequest.builder()
                .name("My Acc")
                .accountType(AccountType.PERSONAL)
                .build();

        var out = service.createAccount("usr-1", req);

        assertThat(out.getAccountNumber()).isNotNull();
        assertThat(out.getAccountNumber()).hasSize(8);
        assertThat(out.getName()).isEqualTo("My Acc");
        assertThat(out.getSortCode()).isEqualTo(SortCode.DEFAULT);
        assertThat(out.getCurrency()).isEqualTo(Currency.GBP);
        assertThat(out.getBalance()).isEqualTo(0.00);

    }

    @Test
    void fetchAccountForUser_whenOwned_returnsAccount() {
        when(repo.findByAccountNumber("012345")).thenReturn(Optional.of(existing()));
        when(repo.findOwnerByAccountNumber("012345")).thenReturn(Optional.of("usr-1"));

        var result = service.fetchAccountForUser("012345", "usr-1");
        assertThat(result.getAccountNumber()).isEqualTo("012345");

    }

    @Test
    void fetchAccountForUser_notFound_throws404() {
        when(repo.findByAccountNumber("nope")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.fetchAccountForUser("nope", "usr-1"));
    }

    @Test
    void fetchAccountForUser_notOwner_throws403() {
        when(repo.findByAccountNumber("012345")).thenReturn(Optional.of(existing()));
        when(repo.findOwnerByAccountNumber("012345")).thenReturn(Optional.of("usr-other"));
        assertThrows(ForbiddenException.class, () -> service.fetchAccountForUser("012345", "usr-1"));
    }

    @Test
    void updateAccountForUser_updatesProvidedFields_only() {
        when(repo.findByAccountNumber("012345")).thenReturn(Optional.of(existing()));
        when(repo.findOwnerByAccountNumber("012345")).thenReturn(Optional.of("usr-1"));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = UpdateBankAccountRequest.builder().name(" New Name ").build();

        var updated = service.updateAccountForUser("012345", "usr-1", req);
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getAccountType()).isEqualTo(AccountType.PERSONAL);
    }

    @Test
    void deleteAccountForUser_deletesAndUnlinks() {
        when(repo.findByAccountNumber("012345")).thenReturn(Optional.of(existing()));
        when(repo.findOwnerByAccountNumber("012345")).thenReturn(Optional.of("usr-1"));

        service.deleteAccountForUser("012345", "usr-1");
    }

    @Test
    void userHasAnyAccount_trueWhenNonEmpty() {
        when(repo.findByUserId("usr-1")).thenReturn(List.of(existing()));
        assertThat(service.userHasAnyAccount("usr-1")).isTrue();
    }

    @Test
    void userHasAnyAccount_falseWhenEmpty() {
        when(repo.findByUserId("usr-1")).thenReturn(List.of());
        assertThat(service.userHasAnyAccount("usr-1")).isFalse();
    }
}