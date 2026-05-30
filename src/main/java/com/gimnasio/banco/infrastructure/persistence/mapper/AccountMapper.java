package com.gimnasio.banco.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gimnasio.banco.domain.model.AccountStatus;
import com.gimnasio.banco.domain.model.BankAccount;
import com.gimnasio.banco.domain.model.Money;
import com.gimnasio.banco.infrastructure.persistence.entity.AccountEntity;

/**
 * MAPPER: Convierte entre BankAccount (domain) y AccountEntity (JPA)
 * 
 * MapStruct genera automáticamente la implementación en tiempo de compilación.
 * 
 * @Mapper: Indica que es un mapper de MapStruct
 *          componentModel = "spring": Registra el mapper como un bean de Spring
 *          injectionStrategy = CONSTRUCTOR: Usa inyección por constructor
 */
@Mapper(componentModel = "spring")
public interface AccountMapper {

    // toEntity() funciona normal (AccountEntity SÍ tiene setters)
    @Mapping(target = "balanceAmount", expression = "java(account.getBalance().getAmount())")
    @Mapping(target = "balanceCurrency", expression = "java(account.getBalance().getCurrency())")
    @Mapping(target = "status", expression = "java(account.getStatus())")
    AccountEntity toEntity(BankAccount account);

    // toDomain() usa método default que llama al constructor ✅
    default BankAccount toDomain(AccountEntity entity) {
        if (entity == null) {
            return null;
        }

        Money balance = Money.of(
                entity.getBalanceAmount(),
                entity.getBalanceCurrency());

        AccountStatus status = entity.getStatus();

        // Usa el constructor en lugar de setters
        return new BankAccount(
                entity.getId(),
                entity.getAccountNumber(),
                entity.getHolderName(),
                balance,
                status);
    }
}