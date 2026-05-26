package com.gimnasio.membership.infrastructure.persistence.entity;

import java.time.LocalDate;

import com.gimnasio.membership.domain.model.EstadoSocio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "socios")
public class SocioEntity {
    @Id
    @Column(name = "socio_id", length = 20)
    private String socioId;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable = false, unique = true, length = 8)
    private String dni;
    @Column(nullable = false, length = 100)
    private String email;
    @Column(name = "contador_faltas")
    private int contadorFaltas;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSocio estado;
    @Column(name = "suspendido_hasta")
    private LocalDate suspendidoHasta;
}
