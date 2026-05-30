package com.gimnasio.booking.domain.model;

import java.time.LocalDate;

import lombok.Getter;

/**
 * AGGREGATE ROOT: ClaseGrupal
 * ────────────────────────────
 * Custodia el inventario físico de aforo.
 *
 * Invariante del dominio (del documento del profe):
 * "No se permite ocupar un cupo si cuposOcupados == capacidadMaxima."
 *
 * Lenguaje Ubicuo:
 * clase.ocuparCupo() ← evento CupoVerificado + ReservaRealizada
 * clase.liberarCupo() ← evento ReservaCancelada
 */
@Getter
public class ClaseGrupal {

    private final String claseId;
    private final String nombre;
    private final String instructor;
    private final Horario horario; // Value Object
    private final LocalDate fecha;
    private final int capacidadMaxima;
    private int cuposOcupados;

    public ClaseGrupal(String claseId, String nombre, String instructor,
            Horario horario, LocalDate fecha, int capacidadMaxima) {
        if (claseId == null || claseId.isBlank())
            throw new IllegalArgumentException("claseId requerido");
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("nombre requerido");
        if (instructor == null || instructor.isBlank())
            throw new IllegalArgumentException("instructor requerido");
        if (horario == null)
            throw new IllegalArgumentException("horario requerido");
        if (fecha == null)
            throw new IllegalArgumentException("fecha requerida");
        if (capacidadMaxima <= 0)
            throw new IllegalArgumentException("capacidad debe ser positiva");

        this.claseId = claseId;
        this.nombre = nombre;
        this.instructor = instructor;
        this.horario = horario;
        this.fecha = fecha;
        this.capacidadMaxima = capacidadMaxima;
        this.cuposOcupados = 0;
    }

    /**
     * REGLA DE NEGOCIO: Ocupar un cupo.
     * Evento: CupoVerificado → ReservaRealizada
     * Invariante: cuposOcupados no puede superar capacidadMaxima.
     */
    public void ocuparCupo() {
        if (estaLlena()) {
            throw new IllegalStateException(
                    "La clase '" + nombre + "' está llena (" + capacidadMaxima + "/" + capacidadMaxima + " cupos)");
        }
        this.cuposOcupados++;
    }

    /**
     * REGLA DE NEGOCIO: Liberar un cupo.
     * Evento: ReservaCancelada
     */
    public void liberarCupo() {
        if (cuposOcupados <= 0) {
            throw new IllegalStateException("No hay cupos ocupados para liberar en clase: " + claseId);
        }
        this.cuposOcupados--;
    }

    /** ¿La clase está al límite de aforo? */
    public boolean estaLlena() {
        return cuposOcupados >= capacidadMaxima;
    }

    /** ¿Cuántos cupos quedan disponibles? */
    public int cuposDisponibles() {
        return capacidadMaxima - cuposOcupados;
    }

    @Override
    public String toString() {
        return "ClaseGrupal{id='" + claseId + "', nombre='" + nombre
                + "', " + fecha + " " + horario
                + ", cupos=" + cuposOcupados + "/" + capacidadMaxima + "}";
    }
}
