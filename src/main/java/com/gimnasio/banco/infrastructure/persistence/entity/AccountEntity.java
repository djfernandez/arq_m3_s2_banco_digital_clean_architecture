package com.gimnasio.banco.infrastructure.persistence.entity;

import java.math.BigDecimal;

import com.gimnasio.banco.domain.model.AccountStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA ENTITY (tabla de BD)
 * 
 * Usando Lombok:
 * 
 * @Data: Genera getters, setters, equals, hashCode, toString
 * @Builder: Patrón Builder
 * @NoArgsConstructor: Constructor vacío para JPA
 * @AllArgsConstructor: Constructor con todos los campos para Builder
 */
@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String holderName;

    @Column(nullable = false)
    private BigDecimal balanceAmount;

    @Column(nullable = false)
    private String balanceCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;
}
