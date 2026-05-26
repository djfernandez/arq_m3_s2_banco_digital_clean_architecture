package com.gimnasio.membership.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Command: registrar un nuevo socio */
@Getter
@AllArgsConstructor
public class RegistrarSocioCommand {
    private final String nombre;
    private final String dni;
    private final String email;
}
