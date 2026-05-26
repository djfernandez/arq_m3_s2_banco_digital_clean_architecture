package com.gimnasio.membership.application.dto;

import java.time.LocalDate;

import com.gimnasio.membership.domain.model.DuracionPlan;
import com.gimnasio.membership.domain.model.TipoPlan;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Command: crear una membresía para un socio */
@Getter
@AllArgsConstructor
public class CrearMembresiaCommand {
    private final String socioId;
    private final TipoPlan tipoPlan;
    private final DuracionPlan duracion;
    private final LocalDate fechaInicio;
}
