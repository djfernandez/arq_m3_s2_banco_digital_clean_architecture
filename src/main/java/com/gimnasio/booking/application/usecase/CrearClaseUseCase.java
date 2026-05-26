package com.gimnasio.booking.application.usecase;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gimnasio.booking.domain.model.ClaseGrupal;
import com.gimnasio.booking.domain.model.Horario;
import com.gimnasio.booking.domain.repository.ClaseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CASO DE USO: Crear Clase Grupal
 * Evento generado: ClaseGrupalCreada
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrearClaseUseCase {

    private final ClaseRepository claseRepository;

    @Transactional
    public ClaseGrupal execute(String nombre, String instructor,
            Horario horario, LocalDate fecha, int capacidad) {
        String claseId = "CLS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ClaseGrupal clase = new ClaseGrupal(claseId, nombre, instructor, horario, fecha, capacidad);
        ClaseGrupal saved = claseRepository.save(clase);

        log.info("Evento: ClaseGrupalCreada — {} '{}' {}", claseId, nombre, fecha);
        return saved;
    }
}
