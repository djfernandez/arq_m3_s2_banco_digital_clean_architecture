package com.gimnasio.membership.infrastructure.persistence.entity;

import java.time.LocalDate;

import com.gimnasio.membership.domain.model.DuracionPlan;
import com.gimnasio.membership.domain.model.EstadoMembresia;
import com.gimnasio.membership.domain.model.TipoPlan;

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
@Table(name = "membresias")
public class MembresiaEntity {
    @Id
    @Column(name = "membresia_id", length = 20)
    private String membresiaId;
    @Column(name = "socio_id", nullable = false, length = 20)
    private String socioId;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_plan", nullable = false, length = 20)
    private TipoPlan tipoPlan;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DuracionPlan duracion;
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;
    @Column(name = "dias_congelados")
    private int diasCongelados;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMembresia estado;
}
