package com.gimnasio.membership.infrastructure.web.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gimnasio.membership.application.dto.CrearMembresiaCommand;
import com.gimnasio.membership.application.dto.RegistrarSocioCommand;
import com.gimnasio.membership.application.usecase.CongelarMembresiaUseCase;
import com.gimnasio.membership.application.usecase.ConsultarMembresiaUseCase;
import com.gimnasio.membership.application.usecase.CrearMembresiaUseCase;
import com.gimnasio.membership.application.usecase.RegistrarSocioUseCase;
import com.gimnasio.membership.application.usecase.RenovarMembresiaUseCase;
import com.gimnasio.membership.domain.exception.MembresiaNotFoundException;
import com.gimnasio.membership.domain.exception.SocioNotFoundException;
import com.gimnasio.membership.domain.model.DuracionPlan;
import com.gimnasio.membership.domain.model.Membresia;
import com.gimnasio.membership.domain.model.Socio;
import com.gimnasio.membership.domain.model.TipoPlan;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CONTROLADOR REST: Membership Context
 * Endpoints:
 * POST /api/socios → Registrar socio (SocioRegistrado)
 * GET /api/socios → Listar socios
 * POST /api/membresias → Crear membresía (MembresiaCreada)
 * GET /api/membresias/{id} → Consultar membresía (MembresiaConsultada)
 * PUT /api/membresias/{id}/renovar → Renovar (MembresiaRenovada)
 * PUT /api/membresias/{id}/congelar → Congelar (MembresiaCongelada)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final RegistrarSocioUseCase registrarSocioUseCase;
    private final CrearMembresiaUseCase crearMembresiaUseCase;
    private final ConsultarMembresiaUseCase consultarMembresiaUseCase;
    private final RenovarMembresiaUseCase renovarMembresiaUseCase;
    private final CongelarMembresiaUseCase congelarMembresiaUseCase;

    // ── Socios ──────────────────────────────────────────────────────

    @PostMapping("/api/socios")
    public ResponseEntity<Map<String, Object>> registrarSocio(@Valid @RequestBody SocioRequest req) {
        Socio socio = registrarSocioUseCase.execute(
                new RegistrarSocioCommand(req.getNombre(), req.getDni(), req.getEmail()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "socioId", socio.getSocioId(),
                "nombre", socio.getNombre(),
                "estado", socio.getEstado()));
    }

    // ── Membresías ──────────────────────────────────────────────────

    @PostMapping("/api/membresias")
    public ResponseEntity<Map<String, Object>> crearMembresia(@Valid @RequestBody MembresiaRequest req) {
        Membresia m = crearMembresiaUseCase.execute(new CrearMembresiaCommand(
                req.getSocioId(), req.getTipoPlan(), req.getDuracion(),
                req.getFechaInicio() != null ? req.getFechaInicio() : LocalDate.now()));
        return ResponseEntity.status(HttpStatus.CREATED).body(membresiaToMap(m));
    }

    @GetMapping("/api/membresias/{id}")
    public ResponseEntity<Map<String, Object>> consultarMembresia(@PathVariable String id) {
        return ResponseEntity.ok(membresiaToMap(consultarMembresiaUseCase.execute(id)));
    }

    @GetMapping("/api/membresias/socio/{socioId}")
    public ResponseEntity<?> membresiasDeSocio(@PathVariable String socioId) {
        return ResponseEntity.ok(consultarMembresiaUseCase.findBySocio(socioId).stream()
                .map(this::membresiaToMap).toList());
    }

    @PutMapping("/api/membresias/{id}/renovar")
    public ResponseEntity<Map<String, Object>> renovar(@PathVariable String id) {
        return ResponseEntity.ok(membresiaToMap(renovarMembresiaUseCase.execute(id)));
    }

    @PutMapping("/api/membresias/{id}/congelar")
    public ResponseEntity<Map<String, Object>> congelar(@PathVariable String id,
            @RequestParam int dias) {
        return ResponseEntity.ok(membresiaToMap(congelarMembresiaUseCase.execute(id, dias)));
    }

    // ── Error handlers ──────────────────────────────────────────────

    @ExceptionHandler(SocioNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleSocioNotFound(SocioNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MembresiaNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleMembresiaNotFound(MembresiaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    private Map<String, Object> membresiaToMap(Membresia m) {
        return Map.of(
                "membresiaId", m.getMembresiaId(),
                "socioId", m.getSocioId(),
                "tipoPlan", m.getTipoPlan(),
                "duracion", m.getDuracion(),
                "fechaInicio", m.getFechaInicio().toString(),
                "fechaFin", m.getFechaFin().toString(),
                "estado", m.getEstado(),
                "diasCongelados", m.getDiasCongelados());
    }

    // ── Request DTOs ────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    public static class SocioRequest {
        @NotBlank
        private String nombre;
        @NotBlank
        private String dni;
        @NotBlank
        private String email;
    }

    @Data
    @NoArgsConstructor
    public static class MembresiaRequest {
        @NotBlank
        private String socioId;
        @NotNull
        private TipoPlan tipoPlan;
        @NotNull
        private DuracionPlan duracion;
        private LocalDate fechaInicio;
    }
}
