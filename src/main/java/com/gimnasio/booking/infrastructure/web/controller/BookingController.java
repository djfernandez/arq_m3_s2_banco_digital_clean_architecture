package com.gimnasio.booking.infrastructure.web.controller;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gimnasio.booking.application.dto.ClaseCommand;
import com.gimnasio.booking.application.dto.CrearClaseCommand;
import com.gimnasio.booking.application.usecase.CancelarReservaUseCase;
import com.gimnasio.booking.application.usecase.CrearClaseUseCase;
import com.gimnasio.booking.application.usecase.RegistrarInasistenciaUseCase;
import com.gimnasio.booking.application.usecase.ReservarClaseUseCase;
import com.gimnasio.booking.domain.exception.ClaseNotFoundException;
import com.gimnasio.booking.domain.exception.ReservaNotFoundException;
import com.gimnasio.booking.domain.model.ClaseGrupal;
import com.gimnasio.booking.domain.model.Horario;
import com.gimnasio.booking.domain.model.Reserva;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CONTROLADOR REST: Booking & Class Context
 * Endpoints:
 * POST /api/clases → Crear clase (ClaseGrupalCreada)
 * GET /api/clases → Listar clases
 * POST /api/reservas → Reservar clase (ReservaRealizada)
 * DELETE /api/reservas/{id} → Cancelar reserva (ReservaCancelada)
 * POST /api/reservas/inasistencia → Registrar No-Show (InasistenciaRegistrada)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class BookingController {

    private final CrearClaseUseCase crearClaseUseCase;
    private final ReservarClaseUseCase reservarClaseUseCase;
    private final CancelarReservaUseCase cancelarReservaUseCase;
    private final RegistrarInasistenciaUseCase registrarInasistenciaUseCase;
    private final com.gimnasio.booking.domain.repository.ClaseRepository claseRepository;

    // ── Clases Grupales ─────────────────────────────────────────────

    @PostMapping("/api/clases")
    public ResponseEntity<Map<String, Object>> crearClase(@Valid @RequestBody ClaseRequest req) {
        Horario.Turno turno = parseTurno(req.getTurno());
        Horario horario = new Horario(req.getHoraInicio(), req.getHoraFin(), turno);
        CrearClaseCommand command = new CrearClaseCommand(
                req.getNombre(), req.getInstructor(), horario, req.getFecha(), req.getCapacidad());

        ClaseGrupal clase = crearClaseUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(claseToMap(clase));
    }

    @GetMapping("/api/clases")
    public ResponseEntity<List<Map<String, Object>>> listarClases() {
        return ResponseEntity.ok(claseRepository.findAll().stream().map(this::claseToMap).toList());
    }

    @GetMapping("/api/clases/{id}")
    public ResponseEntity<Map<String, Object>> getClase(@PathVariable String id) {
        return claseRepository.findById(id)
                .map(c -> ResponseEntity.ok(claseToMap(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ── Reservas ────────────────────────────────────────────────────

    @PostMapping("/api/reservas")
    public ResponseEntity<Map<String, Object>> reservar(@Valid @RequestBody ReservaRequest req) {
        ClaseCommand command = new ClaseCommand(req.getSocioId(), req.getClaseId());
        Reserva reserva = reservarClaseUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaToMap(reserva));
    }

    @DeleteMapping("/api/reservas/{id}")
    public ResponseEntity<Map<String, Object>> cancelar(@PathVariable String id) {
        Reserva reserva = cancelarReservaUseCase.execute(id);
        return ResponseEntity.ok(reservaToMap(reserva));
    }

    @PostMapping("/api/reservas/inasistencia")
    public ResponseEntity<Map<String, Object>> registrarInasistencia(
            @RequestParam String socioId, @RequestParam String claseId) {
        if (socioId.isBlank() || claseId.isBlank()) {
            throw new IllegalArgumentException("socioId y claseId son obligatorios");
        }
        ClaseCommand command = new ClaseCommand(socioId, claseId);
        boolean suspendido = registrarInasistenciaUseCase.execute(command);
        return ResponseEntity.ok(Map.of(
                "status", "InasistenciaRegistrada",
                "socioId", socioId,
                "suspendido", suspendido));
    }

    // ── Error handlers ──────────────────────────────────────────────

    @ExceptionHandler(ClaseNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleClaseNotFound(ClaseNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ReservaNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleReservaNotFound(ReservaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    // ── Helpers ─────────────────────────────────────────────────────

    private Horario.Turno parseTurno(String turnoRaw) {
        if (turnoRaw == null) {
            throw new IllegalArgumentException("El turno es obligatorio");
        }

        String normalized = Normalizer.normalize(turnoRaw.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toUpperCase(Locale.ROOT);

        return switch (normalized) {
            case "MANANA" -> Horario.Turno.MANANA;
            case "TARDE" -> Horario.Turno.TARDE;
            default -> throw new IllegalArgumentException("Turno inválido. Use MANANA o TARDE");
        };
    }

    private Map<String, Object> claseToMap(ClaseGrupal c) {
        return Map.of(
                "claseId", c.getClaseId(),
                "nombre", c.getNombre(),
                "instructor", c.getInstructor(),
                "fecha", c.getFecha().toString(),
                "horario", c.getHorario().toString(),
                "capacidadMaxima", c.getCapacidadMaxima(),
                "cuposDisponibles", c.cuposDisponibles());
    }

    private Map<String, Object> reservaToMap(Reserva r) {
        return Map.of(
                "reservaId", r.getReservaId(),
                "socioId", r.getSocioId(),
                "claseId", r.getClaseId(),
                "fechaHoraReserva", r.getFechaHoraReserva().toString(),
                "estado", r.getEstado());
    }

    // ── Request DTOs ────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    public static class ClaseRequest {
        @NotBlank
        private String nombre;
        @NotBlank
        private String instructor;
        @NotNull
        private LocalTime horaInicio;
        @NotNull
        private LocalTime horaFin;
        @NotBlank
        private String turno; // MANANA | TARDE
        @NotNull
        private LocalDate fecha;
        @Positive
        private int capacidad;
    }

    @Data
    @NoArgsConstructor
    public static class ReservaRequest {
        @NotBlank
        private String socioId;
        @NotBlank
        private String claseId;
    }
}
