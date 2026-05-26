package com.gimnasio.membership.domain.model;

import java.time.LocalDate;

import lombok.Getter;

/**
 * AGGREGATE ROOT: Membresía
 * ──────────────────────────
 * Controla fechas, estados y pausas del contrato.
 *
 * Invariantes del dominio (del documento del profe):
 * 1. Solo el estado ACTIVA faculta la reserva de clases.
 * 2. Rechaza congelamiento si diasCongelados > 60 acumulados por año.
 *
 * Lenguaje Ubicuo aplicado:
 * membresia.estaActiva() ← nombre de negocio
 * membresia.congelar(dias) ← verbo del dominio
 * membresia.renovar() ← evento MembresiaRenovada
 */
@Getter
public class Membresia {

    private static final int MAX_DIAS_CONGELADOS_POR_ANIO = 60;

    private final String membresiaId;
    private final String socioId;
    private final TipoPlan tipoPlan;
    private final DuracionPlan duracion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int diasCongelados;
    private EstadoMembresia estado;

    public Membresia(String membresiaId, String socioId,
            TipoPlan tipoPlan, DuracionPlan duracion,
            LocalDate fechaInicio) {
        if (membresiaId == null || membresiaId.isBlank())
            throw new IllegalArgumentException("membresiaId requerido");
        if (socioId == null || socioId.isBlank())
            throw new IllegalArgumentException("socioId requerido");
        if (tipoPlan == null)
            throw new IllegalArgumentException("tipoPlan requerido");
        if (duracion == null)
            throw new IllegalArgumentException("duracion requerida");
        if (fechaInicio == null)
            throw new IllegalArgumentException("fechaInicio requerida");

        this.membresiaId = membresiaId;
        this.socioId = socioId;
        this.tipoPlan = tipoPlan;
        this.duracion = duracion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaInicio.plusMonths(duracion.getMeses());
        this.diasCongelados = 0;
        this.estado = EstadoMembresia.ACTIVA;
    }

    /**
     * REGLA DE NEGOCIO: ¿Está activa la membresía hoy?
     * Verifica vencimiento automático al consultar.
     */
    public boolean estaActiva() {
        actualizarEstadoSiVencio();
        return estado == EstadoMembresia.ACTIVA;
    }

    /**
     * REGLA DE NEGOCIO: ¿Este plan permite reservar clases grupales?
     */
    public boolean permiteReservarClases() {
        return estaActiva() && tipoPlan.permiteClasesGrupales();
    }

    /**
     * REGLA DE NEGOCIO: Renovar membresía.
     * Extiende la fecha de fin según la duración del plan.
     * Evento: MembresiaRenovada
     */
    public void renovar() {
        if (estado == EstadoMembresia.CONGELADA) {
            throw new IllegalStateException("No se puede renovar una membresía congelada. Descongélela primero.");
        }
        // Si está vencida, renueva desde hoy; si está activa, extiende desde fechaFin
        LocalDate base = (estado == EstadoMembresia.VENCIDA) ? LocalDate.now() : fechaFin;
        this.fechaFin = base.plusMonths(duracion.getMeses());
        this.estado = EstadoMembresia.ACTIVA;
    }

    /**
     * REGLA DE NEGOCIO: Congelar membresía.
     * Invariante: no puede superar 60 días congelados por año.
     * Evento: MembresiaCongelada
     */
    public void congelar(int dias) {
        actualizarEstadoSiVencio();
        if (estado != EstadoMembresia.ACTIVA) {
            throw new IllegalStateException("Solo se puede congelar una membresía ACTIVA.");
        }
        if (dias <= 0) {
            throw new IllegalArgumentException("Los días de congelamiento deben ser positivos.");
        }
        if ((this.diasCongelados + dias) > MAX_DIAS_CONGELADOS_POR_ANIO) {
            throw new IllegalStateException(
                    "Límite de congelamiento excedido. Días disponibles: "
                            + (MAX_DIAS_CONGELADOS_POR_ANIO - this.diasCongelados));
        }
        this.diasCongelados += dias;
        this.fechaFin = this.fechaFin.plusDays(dias); // extiende la vigencia
        this.estado = EstadoMembresia.CONGELADA;
    }

    /**
     * REGLA DE NEGOCIO: Descongelar membresía.
     * Evento: MembresiaDescongelada
     */
    public void descongelar() {
        if (estado != EstadoMembresia.CONGELADA) {
            throw new IllegalStateException("La membresía no está congelada.");
        }
        this.estado = EstadoMembresia.ACTIVA;
    }

    /**
     * ¿La membresía vence en los próximos N días?
     * Usado para disparar alertas de vencimiento.
     */
    public boolean venceEnDias(int dias) {
        return estaActiva() && !LocalDate.now().plusDays(dias).isBefore(fechaFin);
    }

    private void actualizarEstadoSiVencio() {
        if (estado == EstadoMembresia.ACTIVA && LocalDate.now().isAfter(fechaFin)) {
            this.estado = EstadoMembresia.VENCIDA;
        }
    }

    @Override
    public String toString() {
        return "Membresia{id='" + membresiaId + "', plan=" + tipoPlan
                + ", estado=" + estado + ", hasta=" + fechaFin + "}";
    }
}
