package com.gimnasio.membership.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gimnasio.membership.domain.exception.MembresiaNotFoundException;
import com.gimnasio.membership.domain.model.Membresia;
import com.gimnasio.membership.domain.repository.MembresiaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CASO DE USO: Congelar Membresía
 * Evento generado: MembresiaCongelada
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CongelarMembresiaUseCase {

    private final MembresiaRepository membresiaRepository;

    @Transactional
    public Membresia execute(String membresiaId, int dias) {
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new MembresiaNotFoundException(membresiaId));

        membresia.congelar(dias);
        Membresia saved = membresiaRepository.save(membresia);
        log.info("Evento: MembresiaCongelada — {} por {} días", membresiaId, dias);
        return saved;
    }
}
