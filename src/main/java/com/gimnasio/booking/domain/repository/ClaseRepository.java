package com.gimnasio.booking.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.gimnasio.booking.domain.model.ClaseGrupal;

public interface ClaseRepository {
    ClaseGrupal save(ClaseGrupal clase);

    Optional<ClaseGrupal> findById(String claseId);

    List<ClaseGrupal> findAll();

    List<ClaseGrupal> findByFecha(LocalDate fecha);
}
