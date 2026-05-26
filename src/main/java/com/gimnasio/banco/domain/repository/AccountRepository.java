package com.gimnasio.banco.domain.repository;

import java.util.Optional;

import com.gimnasio.banco.domain.model.BankAccount;

/**
 * REPOSITORY INTERFACE
 */
public interface AccountRepository {

    BankAccount save(BankAccount account);

    Optional<BankAccount> findById(Long id);

    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
