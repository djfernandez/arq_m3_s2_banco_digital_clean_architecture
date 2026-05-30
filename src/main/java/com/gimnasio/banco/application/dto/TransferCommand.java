package com.gimnasio.banco.application.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * COMMAND: Datos de entrada para transferencia
 * 
 * Usando Lombok para reducir boilerplate
 */
@Getter
@AllArgsConstructor
public class TransferCommand {

    private final String fromAccountNumber;
    private final String toAccountNumber;
    private final BigDecimal amount;
    private final String currency;
}
