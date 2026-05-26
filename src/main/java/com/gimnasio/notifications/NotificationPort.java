package com.gimnasio.notifications;

import java.time.LocalDate;

import com.gimnasio.booking.domain.model.Horario;

/**
 * PUERTO DE NOTIFICACIONES (Gym Notifications Context)
 * ─────────────────────────────────────────────────────
 * Published Language: contrato común que los contextos Core
 * usan para publicar eventos al contexto Supporting de Notificaciones.
 *
 * Patrón del Context Map del profe:
 * "Bus de eventos en formato plano; Notificaciones no conoce el core
 * de reservas, solo interpreta el contrato del evento publicado."
 */
public interface NotificationPort {

    // ── Membership Context events ──
    void notificarMembresiaCreada(String nombreSocio, String membresiaId, LocalDate fechaFin);

    void notificarVencimientoProximo(String socioId, LocalDate fechaVencimiento);

    void notificarSocioSuspendido(String nombreSocio, LocalDate suspendidoHasta);

    // ── Booking Context events ──
    void notificarReservaConfirmada(String nombreSocio, String nombreClase, LocalDate fecha, Horario horario);

    void notificarReservaCancelada(String nombreSocio, String nombreClase, LocalDate fecha);
}
