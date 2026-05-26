package com.gimnasio.banco.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.gimnasio.banco.domain.model.BankAccount;
import com.gimnasio.banco.domain.repository.AccountRepository;
import com.gimnasio.banco.infrastructure.persistence.entity.AccountEntity;
import com.gimnasio.banco.infrastructure.persistence.mapper.AccountMapper;
import com.gimnasio.banco.infrastructure.persistence.repository.JpaAccountRepository;

import java.util.Optional;

/**
 * ADAPTER: Implementa AccountRepository usando JPA
 * 
 * VENTAJA DE MAPSTRUCT:
 * El código de conversión se genera automáticamente.
 * NO necesitamos escribir manualmente toEntity() y toDomain().
 * 
 * @RequiredArgsConstructor: Lombok genera el constructor
 */
@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepository {

    private final JpaAccountRepository jpaRepository;
    private final AccountMapper mapper; // ← Inyectado por Spring (gracias a MapStruct)

    @Override
    public BankAccount save(BankAccount account) {
        AccountEntity entity = mapper.toEntity(account);
        AccountEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<BankAccount> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber)
                .map(mapper::toDomain);
    }
}
