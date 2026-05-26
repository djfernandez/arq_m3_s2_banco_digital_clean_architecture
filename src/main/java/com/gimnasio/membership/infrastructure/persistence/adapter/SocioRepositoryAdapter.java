package com.gimnasio.membership.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gimnasio.membership.domain.model.Dni;
import com.gimnasio.membership.domain.model.Socio;
import com.gimnasio.membership.domain.repository.SocioRepository;
import com.gimnasio.membership.infrastructure.persistence.entity.SocioEntity;
import com.gimnasio.membership.infrastructure.persistence.repository.JpaSocioRepository;

import lombok.RequiredArgsConstructor;

/**
 * ADAPTADOR: SocioRepositoryAdapter
 * Implementa el puerto SocioRepository usando JPA.
 */
@Component
@RequiredArgsConstructor
public class SocioRepositoryAdapter implements SocioRepository {

    private final JpaSocioRepository jpaRepository;

    @Override
    public Socio save(Socio socio) {
        return toDomain(jpaRepository.save(toEntity(socio)));
    }

    @Override
    public Optional<Socio> findById(String socioId) {
        return jpaRepository.findById(socioId).map(this::toDomain);
    }

    @Override
    public Optional<Socio> findByDni(String dni) {
        return jpaRepository.findByDni(dni).map(this::toDomain);
    }

    @Override
    public List<Socio> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsById(String socioId) {
        return jpaRepository.existsById(socioId);
    }

    private SocioEntity toEntity(Socio s) {
        return SocioEntity.builder()
                .socioId(s.getSocioId())
                .nombre(s.getNombre())
                .dni(s.getDni().getValor())
                .email(s.getEmail())
                .contadorFaltas(s.getContadorFaltas())
                .estado(s.getEstado())
                .suspendidoHasta(s.getSuspendidoHasta())
                .build();
    }

    /** Reconstruye el objeto de dominio desde la entidad JPA */
    private Socio toDomain(SocioEntity e) {
        // Creamos el Socio base
        Socio socio = new Socio(e.getSocioId(), e.getNombre(), new Dni(e.getDni()), e.getEmail());
        // Restauramos el estado tal como está en BD usando reflexión de campo
        try {
            var contadorField = Socio.class.getDeclaredField("contadorFaltas");
            contadorField.setAccessible(true);
            contadorField.set(socio, e.getContadorFaltas());

            var estadoField = Socio.class.getDeclaredField("estado");
            estadoField.setAccessible(true);
            estadoField.set(socio, e.getEstado());

            var suspendidoField = Socio.class.getDeclaredField("suspendidoHasta");
            suspendidoField.setAccessible(true);
            suspendidoField.set(socio, e.getSuspendidoHasta());
        } catch (Exception ex) {
            throw new RuntimeException("Error restaurando Socio desde BD", ex);
        }
        return socio;
    }
}
