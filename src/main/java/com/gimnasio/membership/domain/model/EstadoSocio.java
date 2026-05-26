package com.gimnasio.membership.domain.model;

/** Estados del Socio según el Lenguaje Ubicuo del profe */
public enum EstadoSocio {
    ACTIVO,
    SUSPENDIDO  // penalización de 7 días al acumular 3 faltas (No-Show)
}
