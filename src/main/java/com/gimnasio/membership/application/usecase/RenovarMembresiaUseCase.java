package com.gimnasio.membership.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gimnasio.membership.domain.exception.MembresiaNotFoundException;
import com.gimnasio.membership.domain.model.Membresia;
import com.gimnasio.membership.domain.repository.MembresiaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CASO DE USO: Renovar Membresía
 * Evento generado: MembresiaRenovada
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RenovarMembresiaUseCase {

    private final MembresiaRepository membresiaRepository;

    @Transactional
    public Membresia execute(String membresiaId) {
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new MembresiaNotFoundException(membresiaId));

        membresia.renovar();
        Membresia saved = membresiaRepository.save(membresia);
        log.info("Evento: MembresiaRenovada — {} hasta {}", membresiaId, saved.getFechaFin());
        return saved;
    }
}
