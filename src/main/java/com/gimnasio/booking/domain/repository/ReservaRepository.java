package com.gimnasio.booking.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.gimnasio.booking.domain.model.Reserva;

public interface ReservaRepository {
    Reserva save(Reserva reserva);

    Optional<Reserva> findById(String reservaId);

    List<Reserva> findBySocioId(String socioId);

    List<Reserva> findByClaseId(String claseId);

    /** Para validar límite de 3 reservas diarias por socio */
    long countBySocioIdAndFecha(String socioId, LocalDate fecha);

    List<Reserva> findBySocioIdAndClaseId(String socioId, String claseId);
}
