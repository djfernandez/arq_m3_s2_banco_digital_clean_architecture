package com.gimnasio.membership.domain.model;

import java.time.LocalDate;

import lombok.Getter;

/**
 * AGGREGATE ROOT: Socio
 * ──────────────────────
 * Raíz del agregado de socios. Controla la conducta disciplinaria.
 *
 * Invariante del dominio (del documento del profe):
 * "Si contadorFaltas == 3, el estado cambia automáticamente a
 * SUSPENDIDO por 7 días."
 *
 * Lenguaje Ubicuo aplicado: los métodos hablan español del negocio.
 * NO: member.incrementAbsenceCount()
 * SÍ: socio.registrarFalta()
 */
@Getter
public class Socio {

    private static final int MAX_FALTAS_PARA_SUSPENSION = 3;
    private static final int DIAS_SUSPENSION = 7;

    private final String socioId;
    private final String nombre;
    private final Dni dni; // Value Object con validación
    private final String email;
    private int contadorFaltas;
    private EstadoSocio estado;
    private LocalDate suspendidoHasta;

    public Socio(String socioId, String nombre, Dni dni, String email) {
        if (socioId == null || socioId.isBlank())
            throw new IllegalArgumentException("socioId requerido");
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("nombre requerido");
        if (dni == null)
            throw new IllegalArgumentException("dni requerido");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("email requerido");

        this.socioId = socioId;
        this.nombre = nombre;
        this.dni = dni;
        this.email = email;
        this.contadorFaltas = 0;
        this.estado = EstadoSocio.ACTIVO;
        this.suspendidoHasta = null;
    }

    /**
     * REGLA DE NEGOCIO: Registrar una falta (No-Show).
     * Evento: InasistenciaRegistrada → puede disparar SocioSuspendido
     */
    public boolean registrarFalta() {
        validarNoSuspendido();
        this.contadorFaltas++;

        if (this.contadorFaltas >= MAX_FALTAS_PARA_SUSPENSION) {
            suspender();
            return true; // se suspendió
        }
        return false;
    }

    /**
     * REGLA DE NEGOCIO: Suspender al socio 7 días.
     * Invariante: Solo se puede suspender si está ACTIVO.
     */
    public void suspender() {
        this.estado = EstadoSocio.SUSPENDIDO;
        this.suspendidoHasta = LocalDate.now().plusDays(DIAS_SUSPENSION);
        this.contadorFaltas = 0; // reinicia el contador tras la suspensión
    }

    /**
     * Verifica si la suspensión expiró y reactiva al socio.
     * Debe llamarse en cada operación para reflejar el tiempo transcurrido.
     */
    public void verificarYReactivarSiCorresponde() {
        if (estado == EstadoSocio.SUSPENDIDO
                && suspendidoHasta != null
                && !LocalDate.now().isBefore(suspendidoHasta)) {
            this.estado = EstadoSocio.ACTIVO;
            this.suspendidoHasta = null;
        }
    }

    /** ¿El socio puede operar hoy? */
    public boolean estaActivo() {
        verificarYReactivarSiCorresponde();
        return estado == EstadoSocio.ACTIVO;
    }

    private void validarNoSuspendido() {
        verificarYReactivarSiCorresponde();
        if (estado == EstadoSocio.SUSPENDIDO) {
            throw new IllegalStateException(
                    "El socio " + nombre + " está suspendido hasta " + suspendidoHasta);
        }
    }

    @Override
    public String toString() {
        return "Socio{id='" + socioId + "', nombre='" + nombre + "', estado=" + estado + "}";
    }
}
