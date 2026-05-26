package com.gimnasio.membership.domain.model;

/**
 * VALUE OBJECT: DuracionPlan
 * ───────────────────────────
 * Enumera las estructuras estandarizadas de contratación.
 * Definido en el Lenguaje Ubicuo del profe.
 */
public enum DuracionPlan {

    MENSUAL(1),
    TRIMESTRAL(3),
    ANUAL(12);

    private final int meses;

    DuracionPlan(int meses) {
        this.meses = meses;
    }

    public int getMeses() {
        return meses;
    }
}
