package com.gimnasio.membership.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gimnasio.membership.application.dto.RegistrarSocioCommand;
import com.gimnasio.membership.domain.model.Dni;
import com.gimnasio.membership.domain.model.Socio;
import com.gimnasio.membership.domain.repository.SocioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CASO DE USO: Registrar Socio
 * Evento generado: SocioRegistrado
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrarSocioUseCase {

    private final SocioRepository socioRepository;

    @Transactional
    public Socio execute(RegistrarSocioCommand command) {
        log.info("Registrando socio: {} DNI:{}", command.getNombre(), command.getDni());

        // Validar DNI único
        socioRepository.findByDni(command.getDni()).ifPresent(s -> {
            throw new IllegalStateException("Ya existe un socio con DNI: " + command.getDni());
        });

        String socioId = "SOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Dni dni = new Dni(command.getDni());
        Socio socio = new Socio(socioId, command.getNombre(), dni, command.getEmail());

        Socio saved = socioRepository.save(socio);
        log.info("Evento: SocioRegistrado — {}", saved.getSocioId());
        return saved;
    }
}
