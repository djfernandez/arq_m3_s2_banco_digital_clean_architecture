package com.gimnasio.booking.infrastructure.persistence.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gimnasio.booking.domain.model.ClaseGrupal;
import com.gimnasio.booking.domain.model.Horario;
import com.gimnasio.booking.domain.repository.ClaseRepository;
import com.gimnasio.booking.infrastructure.persistence.entity.ClaseEntity;
import com.gimnasio.booking.infrastructure.persistence.repository.JpaClaseRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClaseRepositoryAdapter implements ClaseRepository {

    private final JpaClaseRepository jpaRepository;

    @Override
    public ClaseGrupal save(ClaseGrupal clase) {
        return toDomain(jpaRepository.save(toEntity(clase)));
    }

    @Override
    public Optional<ClaseGrupal> findById(String claseId) {
        return jpaRepository.findById(claseId).map(this::toDomain);
    }

    @Override
    public List<ClaseGrupal> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<ClaseGrupal> findByFecha(LocalDate fecha) {
        return jpaRepository.findByFecha(fecha).stream().map(this::toDomain).collect(Collectors.toList());
    }

    private ClaseEntity toEntity(ClaseGrupal c) {
        return ClaseEntity.builder()
                .claseId(c.getClaseId())
                .nombre(c.getNombre())
                .instructor(c.getInstructor())
                .horaInicio(c.getHorario().getHoraInicio())
                .horaFin(c.getHorario().getHoraFin())
                .turno(c.getHorario().getTurno().name())
                .fecha(c.getFecha())
                .capacidadMaxima(c.getCapacidadMaxima())
                .cuposOcupados(c.getCuposOcupados())
                .build();
    }

    private ClaseGrupal toDomain(ClaseEntity e) {
        Horario horario = new Horario(e.getHoraInicio(), e.getHoraFin(),
                Horario.Turno.valueOf(e.getTurno()));
        ClaseGrupal clase = new ClaseGrupal(
                e.getClaseId(), e.getNombre(), e.getInstructor(),
                horario, e.getFecha(), e.getCapacidadMaxima());
        try {
            var field = ClaseGrupal.class.getDeclaredField("cuposOcupados");
            field.setAccessible(true);
            field.set(clase, e.getCuposOcupados());
        } catch (Exception ex) {
            throw new RuntimeException("Error restaurando ClaseGrupal", ex);
        }
        return clase;
    }
}
