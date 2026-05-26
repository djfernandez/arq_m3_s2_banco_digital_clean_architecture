package com.gimnasio.membership.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * VALUE OBJECT: Dni
 * ─────────────────
 * Encapsula la validación del DNI peruano (8 dígitos exactos).
 * Inmutable por diseño — no tiene setters.
 *
 * Lenguaje ubicuo del profe: "Cadena inmutable validada
 * con formato de identidad oficial de 8 dígitos."
 */
@Getter
@EqualsAndHashCode
public class Dni {

    private final String valor;

    public Dni(String valor) {
        if (valor == null || !valor.matches("\\d{8}")) {
            throw new IllegalArgumentException(
                "DNI inválido: debe contener exactamente 8 dígitos numéricos. Recibido: " + valor
            );
        }
        this.valor = valor;
    }

    @Override
    public String toString() {
        return valor;
    }
}
