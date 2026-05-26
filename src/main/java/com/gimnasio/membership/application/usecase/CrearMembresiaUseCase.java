package com.gimnasio.membership.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gimnasio.membership.application.dto.CrearMembresiaCommand;
import com.gimnasio.membership.domain.exception.SocioNotFoundException;
import com.gimnasio.membership.domain.model.Membresia;
import com.gimnasio.membership.domain.repository.MembresiaRepository;
import com.gimnasio.membership.domain.repository.SocioRepository;
import com.gimnasio.notifications.NotificationPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CASO DE USO: Crear Membresía
 * Evento generado: MembresiaCreada
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrearMembresiaUseCase {

    private final SocioRepository socioRepository;
    private final MembresiaRepository membresiaRepository;
    private final NotificationPort notificationPort;

    @Transactional
    public Membresia execute(CrearMembresiaCommand command) {
        log.info("Creando membresía para socio: {}", command.getSocioId());

        // Verificar que el socio existe
        var socio = socioRepository.findById(command.getSocioId())
                .orElseThrow(() -> new SocioNotFoundException(command.getSocioId()));

        String membresiaId = "MEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Membresia membresia = new Membresia(
                membresiaId,
                command.getSocioId(),
                command.getTipoPlan(),
                command.getDuracion(),
                command.getFechaInicio());

        Membresia saved = membresiaRepository.save(membresia);
        notificationPort.notificarMembresiaCreada(socio.getNombre(), saved.getMembresiaId(), saved.getFechaFin());

        log.info("Evento: MembresiaCreada — {} para {}", saved.getMembresiaId(), socio.getNombre());
        return saved;
    }
}
