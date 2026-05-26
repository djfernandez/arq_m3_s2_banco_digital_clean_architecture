package com.gimnasio.membership.domain.model;

/**
 * VALUE OBJECT: TipoPlan
 * ───────────────────────
 * Catálogo cerrado de planes. Definido en el Lenguaje Ubicuo del profe:
 * - BASICO:   solo gym
 * - PREMIUM:  gym + clases grupales
 * - VIP:      todo + nutricionista
 *
 * Al ser enum, Java garantiza que solo existan estos valores.
 * Ningún código puede crear un TipoPlan inválido.
 */
public enum TipoPlan {

    BASICO("Solo acceso a gym"),
    PREMIUM("Gym + clases grupales"),
    VIP("Todo + nutricionista");

    private final String descripcion;

    TipoPlan(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** ¿Este plan permite reservar clases grupales? */
    public boolean permiteClasesGrupales() {
        return this == PREMIUM || this == VIP;
    }
}
