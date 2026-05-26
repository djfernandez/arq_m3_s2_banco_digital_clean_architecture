package com.gimnasio.booking.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gimnasio.membership.domain.exception.SocioNotFoundException;
import com.gimnasio.membership.domain.model.Socio;
import com.gimnasio.membership.domain.repository.SocioRepository;
import com.gimnasio.notifications.NotificationPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CASO DE USO: Registrar Inasistencia (No-Show)
 * ───────────────────────────────────────────────
 * Evento generado: InasistenciaRegistrada → puede disparar SocioSuspendido
 *
 * "Registrada al concluir la clase; alimenta el contador punitivo del socio."
 * (Lenguaje Ubicuo del profe)
 *
 * Este es el punto de contacto cross-context:
 * Booking Context actúa sobre el Membership Context (Socio)
 * a través del puerto SocioRepository.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrarInasistenciaUseCase {

    private final SocioRepository socioRepository;
    private final NotificationPort notificationPort;

    @Transactional
    public boolean execute(String socioId, String claseId) {
        log.info("Registrando No-Show — socio:{} clase:{}", socioId, claseId);

        Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new SocioNotFoundException(socioId));

        // La regla punitiva vive en el Aggregate Root Socio
        boolean suspendido = socio.registrarFalta();
        socioRepository.save(socio);

        if (suspendido) {
            // Evento: SocioSuspendido
            notificationPort.notificarSocioSuspendido(
                    socio.getNombre(), socio.getSuspendidoHasta());
            log.info("Evento: SocioSuspendido — {} hasta {}", socioId, socio.getSuspendidoHasta());
        } else {
            log.info("Evento: InasistenciaRegistrada — socio:{} faltas:{}/3",
                    socioId, socio.getContadorFaltas());
        }

        return suspendido;
    }
}
