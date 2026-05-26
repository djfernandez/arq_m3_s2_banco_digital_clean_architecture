package com.gimnasio.notifications;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.gimnasio.booking.domain.model.Horario;

/**
 * ADAPTADOR: ConsoleNotificationAdapter
 * ───────────────────────────────────────
 * Implementación del canal consola para el Gym Notifications Context.
 * Requisito 4 del profe: "Soportar 1 canal (consola)."
 *
 * Si mañana se requiere email o SMS, se agrega otro @Component
 * que implemente NotificationPort — sin tocar los contextos Core.
 */
@Component
public class ConsoleNotificationAdapter implements NotificationPort {

    private static final String SEP = "─".repeat(55);
    private static final String SEP2 = "═".repeat(55);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Membership notifications ──────────────────────────────────────

    @Override
    public void notificarMembresiaCreada(String nombreSocio, String membresiaId, LocalDate fechaFin) {
        System.out.println("\n" + SEP);
        System.out.println("  🏋️  MEMBRESÍA CREADA");
        System.out.println("  👤  Socio    : " + nombreSocio);
        System.out.println("  🆔  ID       : " + membresiaId);
        System.out.println("  📅  Vigente hasta: " + fechaFin.format(FMT));
        System.out.println(SEP + "\n");
    }

    @Override
    public void notificarVencimientoProximo(String socioId, LocalDate fechaVencimiento) {
        System.out.println("\n" + SEP2);
        System.out.println("  ⚠️   ALERTA: MEMBRESÍA PRÓXIMA A VENCER");
        System.out.println("  🆔  Socio ID : " + socioId);
        System.out.println("  📅  Vence el : " + fechaVencimiento.format(FMT));
        System.out.println("  💡  Renueve su membresía para continuar reservando clases.");
        System.out.println(SEP2 + "\n");
    }

    @Override
    public void notificarSocioSuspendido(String nombreSocio, LocalDate suspendidoHasta) {
        System.out.println("\n" + SEP2);
        System.out.println("  🚫  SOCIO SUSPENDIDO");
        System.out.println("  👤  Socio    : " + nombreSocio);
        System.out.println("  ⛔  Motivo   : 3 inasistencias sin cancelar (No-Show)");
        System.out.println("  📅  Hasta    : " + suspendidoHasta.format(FMT));
        System.out.println("  📋  Penalización: 7 días sin acceso a servicios");
        System.out.println(SEP2 + "\n");
    }

    // ── Booking notifications ─────────────────────────────────────────

    @Override
    public void notificarReservaConfirmada(String nombreSocio, String nombreClase,
            LocalDate fecha, Horario horario) {
        System.out.println("\n" + SEP);
        System.out.println("  ✅  RESERVA CONFIRMADA");
        System.out.println("  👤  Socio    : " + nombreSocio);
        System.out.println("  🏃  Clase    : " + nombreClase);
        System.out.println("  📅  Fecha    : " + fecha.format(FMT));
        System.out.println("  🕐  Horario  : " + horario);
        System.out.println(SEP + "\n");
    }

    @Override
    public void notificarReservaCancelada(String nombreSocio, String nombreClase, LocalDate fecha) {
        System.out.println("\n" + SEP);
        System.out.println("  ❌  RESERVA CANCELADA");
        System.out.println("  👤  Socio    : " + nombreSocio);
        System.out.println("  🏃  Clase    : " + nombreClase);
        System.out.println("  📅  Fecha    : " + fecha.format(FMT));
        System.out.println(SEP + "\n");
    }
}
