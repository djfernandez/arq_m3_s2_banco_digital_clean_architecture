package com.gimnasio.booking.application.usecase;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gimnasio.booking.domain.exception.ClaseNotFoundException;
import com.gimnasio.booking.domain.model.ClaseGrupal;
import com.gimnasio.booking.domain.model.Reserva;
import com.gimnasio.booking.domain.repository.ClaseRepository;
import com.gimnasio.booking.domain.repository.ReservaRepository;
import com.gimnasio.membership.domain.exception.SocioNotFoundException;
import com.gimnasio.membership.domain.model.Membresia;
import com.gimnasio.membership.domain.repository.MembresiaRepository;
import com.gimnasio.membership.domain.repository.SocioRepository;
import com.gimnasio.notifications.NotificationPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CASO DE USO: Reservar lugar en clase grupal
 * ─────────────────────────────────────────────
 * Implementa TODAS las validaciones del Lenguaje Ubicuo del profe:
 *
 * 1. Socio debe existir y estar ACTIVO (no suspendido)
 * 2. Membresía del socio debe estar ACTIVA
 * 3. El plan debe permitir clases (PREMIUM o VIP)
 * 4. La clase debe tener cupos disponibles
 * 5. Reserva con >= 2 horas de anticipación
 * 6. Límite de 3 reservas por socio por día
 *
 * Patrón: Customer/Supplier entre Membership Context (upstream)
 * y Booking Context (downstream). El Booking consulta el estado
 * del Membership pero no lo modifica directamente.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservarClaseUseCase {

    private static final int MAX_RESERVAS_POR_DIA = 3;
    private static final int HORAS_ANTICIPACION_MIN = 2;

    private final ClaseRepository claseRepository;
    private final ReservaRepository reservaRepository;
    private final SocioRepository socioRepository;
    private final MembresiaRepository membresiaRepository;
    private final NotificationPort notificationPort;

    @Transactional
    public Reserva execute(String socioId, String claseId) {
        log.info("Iniciando reserva — socio:{} clase:{}", socioId, claseId);

        // ── 1. Validar que el socio existe y está activo ──────────────
        var socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new SocioNotFoundException(socioId));

        socio.verificarYReactivarSiCorresponde();
        if (!socio.estaActivo()) {
            throw new IllegalStateException(
                    "El socio " + socio.getNombre() + " está SUSPENDIDO hasta " + socio.getSuspendidoHasta());
        }

        // ── 2. Validar membresía activa con plan que permita clases ───
        Membresia membresia = membresiaRepository.findActivaBySocioId(socioId)
                .orElseThrow(() -> new IllegalStateException(
                        "El socio " + socioId + " no tiene membresía ACTIVA"));

        if (!membresia.permiteReservarClases()) {
            throw new IllegalStateException(
                    "El plan " + membresia.getTipoPlan() + " no incluye clases grupales. "
                            + "Actualice a PREMIUM o VIP.");
        }

        // ── 3. Obtener la clase y verificar cupos ────────────────────
        ClaseGrupal clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new ClaseNotFoundException(claseId));

        if (clase.estaLlena()) {
            throw new IllegalStateException(
                    "La clase '" + clase.getNombre() + "' no tiene cupos disponibles.");
        }

        // ── 4. Validar anticipación mínima de 2 horas ────────────────
        LocalDateTime inicioClase = LocalDateTime.of(clase.getFecha(), clase.getHorario().getHoraInicio());
        long horasRestantes = java.time.Duration.between(LocalDateTime.now(), inicioClase).toHours();
        if (horasRestantes < HORAS_ANTICIPACION_MIN) {
            throw new IllegalStateException(
                    "Reserva con muy poca anticipación. Mínimo " + HORAS_ANTICIPACION_MIN
                            + " horas. Disponibles: " + horasRestantes);
        }

        // ── 5. Validar límite de 3 reservas diarias ──────────────────
        long reservasHoy = reservaRepository.countBySocioIdAndFecha(socioId, clase.getFecha());
        if (reservasHoy >= MAX_RESERVAS_POR_DIA) {
            throw new IllegalStateException(
                    "Límite de " + MAX_RESERVAS_POR_DIA + " reservas diarias alcanzado para el socio " + socioId);
        }

        // ── 6. Ejecutar la reserva (regla de oro del profe) ───────────
        // La Reserva no modifica directamente la Clase, invoca su método
        clase.ocuparCupo(); // ← evento CupoVerificado
        claseRepository.save(clase);

        String reservaId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Reserva reserva = new Reserva(reservaId, socioId, claseId, LocalDateTime.now());
        Reserva saved = reservaRepository.save(reserva);

        // ── 7. Notificar confirmación ──────────────────────────────────
        notificationPort.notificarReservaConfirmada(
                socio.getNombre(), clase.getNombre(), clase.getFecha(), clase.getHorario());

        log.info("Evento: ReservaRealizada — {} | Cupos restantes: {}", reservaId, clase.cuposDisponibles());
        return saved;
    }
}
