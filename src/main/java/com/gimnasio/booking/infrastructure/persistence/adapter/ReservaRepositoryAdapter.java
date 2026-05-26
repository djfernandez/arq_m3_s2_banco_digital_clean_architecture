package com.gimnasio.booking.infrastructure.persistence.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gimnasio.booking.domain.model.Reserva;
import com.gimnasio.booking.domain.repository.ReservaRepository;
import com.gimnasio.booking.infrastructure.persistence.entity.ReservaEntity;
import com.gimnasio.booking.infrastructure.persistence.repository.JpaReservaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservaRepositoryAdapter implements ReservaRepository {

    private final JpaReservaRepository jpaRepository;

    @Override
    public Reserva save(Reserva r) {
        return toDomain(jpaRepository.save(toEntity(r)));
    }

    @Override
    public Optional<Reserva> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Reserva> findBySocioId(String socioId) {
        return jpaRepository.findBySocioId(socioId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Reserva> findByClaseId(String claseId) {
        return jpaRepository.findByClaseId(claseId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countBySocioIdAndFecha(String socioId, LocalDate fecha) {
        return jpaRepository.countBySocioIdAndFecha(socioId, fecha);
    }

    @Override
    public List<Reserva> findBySocioIdAndClaseId(String socioId, String claseId) {
        return jpaRepository.findBySocioIdAndClaseId(socioId, claseId).stream().map(this::toDomain)
                .collect(Collectors.toList());
    }

    private ReservaEntity toEntity(Reserva r) {
        return ReservaEntity.builder()
                .reservaId(r.getReservaId())
                .socioId(r.getSocioId())
                .claseId(r.getClaseId())
                .fechaHoraReserva(r.getFechaHoraReserva())
                .estado(r.getEstado())
                .build();
    }

    private Reserva toDomain(ReservaEntity e) {
        Reserva r = new Reserva(e.getReservaId(), e.getSocioId(), e.getClaseId(), e.getFechaHoraReserva());
        if (e.getEstado() == Reserva.EstadoReserva.CANCELADA) {
            try {
                var field = Reserva.class.getDeclaredField("estado");
                field.setAccessible(true);
                field.set(r, Reserva.EstadoReserva.CANCELADA);
            } catch (Exception ex) {
                throw new RuntimeException("Error restaurando Reserva", ex);
            }
        }
        return r;
    }
}
