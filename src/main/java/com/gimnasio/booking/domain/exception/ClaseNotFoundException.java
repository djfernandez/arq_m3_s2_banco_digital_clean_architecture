package com.gimnasio.booking.domain.exception;

public class ClaseNotFoundException extends RuntimeException {
    private final String claseId;
    public ClaseNotFoundException(String claseId) {
        super("Clase no encontrada: " + claseId);
        this.claseId = claseId;
    }
    public String getClaseId() { return claseId; }
}
