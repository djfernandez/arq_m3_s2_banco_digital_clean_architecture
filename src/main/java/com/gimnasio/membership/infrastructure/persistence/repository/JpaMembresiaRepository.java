package com.gimnasio.membership.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gimnasio.membership.domain.model.EstadoMembresia;
import com.gimnasio.membership.infrastructure.persistence.entity.MembresiaEntity;

@Repository
public interface JpaMembresiaRepository extends JpaRepository<MembresiaEntity, String> {
    List<MembresiaEntity> findBySocioId(String socioId);

    Optional<MembresiaEntity> findBySocioIdAndEstado(String socioId, EstadoMembresia estado);
}
