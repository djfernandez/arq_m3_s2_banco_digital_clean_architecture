package com.gimnasio.booking.application.dto;

import java.time.LocalDate;

import com.gimnasio.booking.domain.model.Horario;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CrearClaseCommand {

  private String nombre;
  private String instructor;
  private Horario horario;
  private LocalDate fecha;
  private int capacidad;

}
