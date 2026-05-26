package com.gimnasio.membership.domain.repository;

import java.util.List;
import java.util.Optional;

import com.gimnasio.membership.domain.model.Socio;

/** Puerto del dominio para persistencia de Socios */
public interface SocioRepository {
    Socio save(Socio socio);

    Optional<Socio> findById(String socioId);

    Optional<Socio> findByDni(String dni);

    List<Socio> findAll();

    boolean existsById(String socioId);
}
