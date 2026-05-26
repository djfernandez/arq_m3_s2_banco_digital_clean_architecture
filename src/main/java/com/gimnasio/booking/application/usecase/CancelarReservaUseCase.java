package com.gimnasio.booking.application.usecase;

import com.gimnasio.booking.domain.exception.ClaseNotFoundException;
import com.gimnasio.booking.domain.exception.ReservaNotFoundException;
import com.gimnasio.booking.domain.model.ClaseGrupal;
import com.gimnasio.booking.domain.model.Reserva;
import com.gimnasio.booking.domain.repository.ClaseRepository;
import com.gimnasio.booking.domain.repository.ReservaRepository;
import com.gimnasio.membership.domain.repository.SocioRepository;
import com.gimnasio.notifications.NotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * CASO DE USO: Cancelar Reserva
 * Evento generado: ReservaCancelada
 * Invariante: cancelación >= 4 horas antes del inicio
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CancelarReservaUseCase {

    private final ReservaRepository reservaRepository;
    private final ClaseRepository   claseRepository;
    private final SocioRepository   socioRepository;
    private final NotificationPort  notificationPort;

    @Transactional
    public Reserva execute(String reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new ReservaNotFoundException(reservaId));

        ClaseGrupal clase = claseRepository.findById(reserva.getClaseId())
            .orElseThrow(() -> new ClaseNotFoundException(reserva.getClaseId()));

        // Calcular inicio de la clase para validar anticipación
        LocalDateTime inicioClase = LocalDateTime.of(
            clase.getFecha(), clase.getHorario().getHoraInicio()
        );

        // La Reserva aplica su propia regla de cancelación (>= 4h)
        reserva.cancelar(inicioClase);

        // Liberar el cupo en la clase
        clase.liberarCupo();
        claseRepository.save(clase);

        Reserva saved = reservaRepository.save(reserva);

        // Notificar cancelación
        var socio = socioRepository.findById(reserva.getSocioId());
        String nombreSocio = socio.map(s -> s.getNombre()).orElse(reserva.getSocioId());
        notificationPort.notificarReservaCancelada(nombreSocio, clase.getNombre(), clase.getFecha());

        log.info("Evento: ReservaCancelada — {} | Cupos liberados en clase: {}", reservaId, clase.getNombre());
        return saved;
    }
}
