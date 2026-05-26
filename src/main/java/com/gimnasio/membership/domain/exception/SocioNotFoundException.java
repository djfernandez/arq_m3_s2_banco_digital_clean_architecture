package com.gimnasio.membership.domain.exception;

public class SocioNotFoundException extends RuntimeException {
    private final String socioId;
    public SocioNotFoundException(String socioId) {
        super("Socio no encontrado: " + socioId);
        this.socioId = socioId;
    }
    public String getSocioId() { return socioId; }
}
