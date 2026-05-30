package com.gimnasio.booking.domain.model;

import java.time.LocalDateTime;

import lombok.Getter;

/**
 * AGGREGATE ROOT: Reserva
 * ────────────────────────
 * "Hecho transaccional independiente que cruza las identidades del negocio."
 * (Definición del profe en el modelo táctico)
 *
 * Invariantes del dominio:
 * - Reservas por socio <= 3 al día
 * - Creación >= 2 horas antes del inicio de la clase
 * - Cancelación >= 4 horas antes del inicio
 *
 * IMPORTANTE: Esta clase NO modifica directamente la ClaseGrupal ni la
 * Membresía.
 * Solo invoca métodos de validación y comando en esos agregados (regla de oro
 * del profe).
 */
@Getter
public class Reserva {

    public enum EstadoReserva {
        CONFIRMADA, CANCELADA
    }

    private final String reservaId;
    private final String socioId;
    private final String claseId;
    private final LocalDateTime fechaHoraReserva;
    private EstadoReserva estado;

    public Reserva(String reservaId, String socioId, String claseId, LocalDateTime fechaHoraReserva) {
        if (reservaId == null || reservaId.isBlank())
            throw new IllegalArgumentException("reservaId requerido");
        if (socioId == null || socioId.isBlank())
            throw new IllegalArgumentException("socioId requerido");
        if (claseId == null || claseId.isBlank())
            throw new IllegalArgumentException("claseId requerido");
        if (fechaHoraReserva == null)
            throw new IllegalArgumentException("fechaHoraReserva requerida");

        this.reservaId = reservaId;
        this.socioId = socioId;
        this.claseId = claseId;
        this.fechaHoraReserva = fechaHoraReserva;
        this.estado = EstadoReserva.CONFIRMADA;
    }

    /**
     * REGLA DE NEGOCIO: Cancelar la reserva.
     * Invariante: debe cancelarse con al menos 4 horas de anticipación.
     * 
     * @param inicioClase fecha/hora de inicio de la clase reservada
     */
    public void cancelar(LocalDateTime inicioClase) {
        if (estado == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("La reserva " + reservaId + " ya está cancelada");
        }
        long horasRestantes = java.time.Duration.between(LocalDateTime.now(), inicioClase).toHours();
        if (horasRestantes < 4) {
            throw new IllegalStateException(
                    "No se puede cancelar con menos de 4 horas de anticipación. Horas restantes: " + horasRestantes);
        }
        this.estado = EstadoReserva.CANCELADA;
    }

    public boolean estaConfirmada() {
        return estado == EstadoReserva.CONFIRMADA;
    }

    @Override
    public String toString() {
        return "Reserva{id='" + reservaId + "', socio='" + socioId
                + "', clase='" + claseId + "', estado=" + estado + "}";
    }
}
