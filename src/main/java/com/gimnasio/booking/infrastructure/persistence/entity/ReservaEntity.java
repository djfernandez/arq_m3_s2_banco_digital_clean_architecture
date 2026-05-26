package com.gimnasio.booking.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import com.gimnasio.booking.domain.model.Reserva.EstadoReserva;

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
@Table(name = "reservas")
public class ReservaEntity {
    @Id
    @Column(name = "reserva_id", length = 20)
    private String reservaId;
    @Column(name = "socio_id", nullable = false, length = 20)
    private String socioId;
    @Column(name = "clase_id", nullable = false, length = 20)
    private String claseId;
    @Column(name = "fecha_hora_reserva", nullable = false)
    private LocalDateTime fechaHoraReserva;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;
}
