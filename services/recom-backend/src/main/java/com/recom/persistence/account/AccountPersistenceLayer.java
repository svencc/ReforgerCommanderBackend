package com.recom.persistence.account;

import com.recom.entity.Account;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.cache.annotation.CacheResult;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountPersistenceLayer {

    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    public Account save(@NonNull final Account account) {
        return accountRepository.save(account);
    }

    @NonNull
    public Optional<Account> findByUUID(@NonNull final UUID uuid) {
        return accountRepository.findById(uuid);
    }

    @NonNull
    public Account createAccount() {
        final Account account = Account.builder()
                .accessKey(UUID.randomUUID().toString())
                .build();

        return accountRepository.save(account);
    }

}