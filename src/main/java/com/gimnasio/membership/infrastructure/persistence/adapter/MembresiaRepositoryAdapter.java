package com.gimnasio.membership.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gimnasio.membership.domain.model.EstadoMembresia;
import com.gimnasio.membership.domain.model.Membresia;
import com.gimnasio.membership.domain.repository.MembresiaRepository;
import com.gimnasio.membership.infrastructure.persistence.entity.MembresiaEntity;
import com.gimnasio.membership.infrastructure.persistence.repository.JpaMembresiaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembresiaRepositoryAdapter implements MembresiaRepository {

    private final JpaMembresiaRepository jpaRepository;

    @Override
    public Membresia save(Membresia m) {
        return toDomain(jpaRepository.save(toEntity(m)));
    }

    @Override
    public Optional<Membresia> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Membresia> findBySocioId(String socioId) {
        return jpaRepository.findBySocioId(socioId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Membresia> findActivaBySocioId(String socioId) {
        return jpaRepository.findBySocioIdAndEstado(socioId, EstadoMembresia.ACTIVA).map(this::toDomain);
    }

    private MembresiaEntity toEntity(Membresia m) {
        return MembresiaEntity.builder()
                .membresiaId(m.getMembresiaId())
                .socioId(m.getSocioId())
                .tipoPlan(m.getTipoPlan())
                .duracion(m.getDuracion())
                .fechaInicio(m.getFechaInicio())
                .fechaFin(m.getFechaFin())
                .diasCongelados(m.getDiasCongelados())
                .estado(m.getEstado())
                .build();
    }

    private Membresia toDomain(MembresiaEntity e) {
        Membresia m = new Membresia(
                e.getMembresiaId(), e.getSocioId(),
                e.getTipoPlan(), e.getDuracion(), e.getFechaInicio());
        try {
            setField(m, "fechaFin", e.getFechaFin());
            setField(m, "diasCongelados", e.getDiasCongelados());
            setField(m, "estado", e.getEstado());
        } catch (Exception ex) {
            throw new RuntimeException("Error restaurando Membresia", ex);
        }
        return m;
    }

    private void setField(Object obj, String name, Object value) throws Exception {
        var field = obj.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
