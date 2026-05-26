package com.gimnasio.membership.domain.repository;

import java.util.List;
import java.util.Optional;

import com.gimnasio.membership.domain.model.Membresia;

/** Puerto del dominio para persistencia de Membresías */
public interface MembresiaRepository {
    Membresia save(Membresia membresia);

    Optional<Membresia> findById(String membresiaId);

    List<Membresia> findBySocioId(String socioId);

    Optional<Membresia> findActivaBySocioId(String socioId);
}
