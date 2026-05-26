package com.gimnasio.booking.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalTime;

/**
 * VALUE OBJECT: Horario
 * ──────────────────────
 * Bloque de tiempo inmutable. Valida la regla de turnos del gimnasio.
 *
 * Lenguaje Ubicuo del profe:
 * "Horarios fijos restringidos a Mañana (6-9 AM) o Tarde (5-8 PM)."
 */
@Getter
@EqualsAndHashCode
public class Horario {

    public enum Turno { MANANA, TARDE }

    private static final LocalTime INICIO_MANANA = LocalTime.of(6, 0);
    private static final LocalTime FIN_MANANA    = LocalTime.of(9, 0);
    private static final LocalTime INICIO_TARDE  = LocalTime.of(17, 0);
    private static final LocalTime FIN_TARDE     = LocalTime.of(20, 0);

    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private final Turno     turno;

    public Horario(LocalTime horaInicio, LocalTime horaFin, Turno turno) {
        if (horaInicio == null || horaFin == null || turno == null) {
            throw new IllegalArgumentException("Todos los campos del horario son obligatorios");
        }
        validarTurno(horaInicio, horaFin, turno);
        this.horaInicio = horaInicio;
        this.horaFin    = horaFin;
        this.turno      = turno;
    }

    /** Factory method para turno mañana */
    public static Horario manana(LocalTime inicio, LocalTime fin) {
        return new Horario(inicio, fin, Turno.MANANA);
    }

    /** Factory method para turno tarde */
    public static Horario tarde(LocalTime inicio, LocalTime fin) {
        return new Horario(inicio, fin, Turno.TARDE);
    }

    private void validarTurno(LocalTime inicio, LocalTime fin, Turno turno) {
        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la de inicio");
        }
        if (turno == Turno.MANANA) {
            if (inicio.isBefore(INICIO_MANANA) || fin.isAfter(FIN_MANANA)) {
                throw new IllegalArgumentException(
                    "El turno Mañana debe estar entre 06:00 y 09:00. Recibido: " + inicio + "-" + fin
                );
            }
        } else {
            if (inicio.isBefore(INICIO_TARDE) || fin.isAfter(FIN_TARDE)) {
                throw new IllegalArgumentException(
                    "El turno Tarde debe estar entre 17:00 y 20:00. Recibido: " + inicio + "-" + fin
                );
            }
        }
    }

    @Override
    public String toString() {
        return turno + " " + horaInicio + "-" + horaFin;
    }
}
