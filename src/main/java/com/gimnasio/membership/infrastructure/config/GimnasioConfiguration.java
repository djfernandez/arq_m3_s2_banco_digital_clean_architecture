package com.gimnasio.membership.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * CONFIGURACIÓN: Gimnasio
 * ─────────────────────────
 * En este proyecto los servicios de dominio no necesitan
 * registro manual porque toda la lógica está encapsulada
 * en los Aggregate Roots (Socio, Membresia, ClaseGrupal, Reserva).
 *
 * Los Use Cases se registran automáticamente con @Service.
 * Los Adapters con @Component.
 * Spring Data JPA genera los repositorios en tiempo de ejecución.
 */
@Configuration
public class GimnasioConfiguration {
    // Todos los beans se registran via anotaciones:
    // @Service  → Use Cases
    // @Component → Adapters + ConsoleNotificationAdapter
    // @Repository → JPA interfaces (generadas por Spring Data)
}
