package com.gimnasio.booking.infrastructure.persistence.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "clases_grupales")
public class ClaseEntity {
    @Id
    @Column(name = "clase_id", length = 20)
    private String claseId;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable = false, length = 100)
    private String instructor;
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
    @Column(nullable = false, length = 10)
    private String turno;
    @Column(nullable = false)
    private LocalDate fecha;
    @Column(name = "capacidad_maxima", nullable = false)
    private int capacidadMaxima;
    @Column(name = "cupos_ocupados", nullable = false)
    private int cuposOcupados;
}
