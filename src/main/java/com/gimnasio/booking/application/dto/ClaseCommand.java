package com.gimnasio.booking.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClaseCommand {
  private String socioId;
  private String claseId;
}
