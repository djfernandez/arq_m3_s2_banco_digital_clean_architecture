package com.gimnasio.membership.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gimnasio.membership.infrastructure.persistence.entity.SocioEntity;

@Repository
public interface JpaSocioRepository extends JpaRepository<SocioEntity, String> {
    Optional<SocioEntity> findByDni(String dni);
}
