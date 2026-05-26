package com.gimnasio.booking.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gimnasio.booking.infrastructure.persistence.entity.ReservaEntity;

@Repository
public interface JpaReservaRepository extends JpaRepository<ReservaEntity, String> {
    List<ReservaEntity> findBySocioId(String socioId);

    List<ReservaEntity> findByClaseId(String claseId);

    List<ReservaEntity> findBySocioIdAndClaseId(String socioId, String claseId);

    @Query("SELECT COUNT(r) FROM ReservaEntity r JOIN ClaseEntity c ON r.claseId = c.claseId " +
            "WHERE r.socioId = :socioId AND c.fecha = :fecha AND r.estado = 'CONFIRMADA'")
    long countBySocioIdAndFecha(String socioId, LocalDate fecha);
}
