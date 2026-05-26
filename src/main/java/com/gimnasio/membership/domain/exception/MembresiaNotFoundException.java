package com.gimnasio.membership.domain.exception;

public class MembresiaNotFoundException extends RuntimeException {
    private final String membresiaId;
    public MembresiaNotFoundException(String membresiaId) {
        super("Membresía no encontrada: " + membresiaId);
        this.membresiaId = membresiaId;
    }
    public String getMembresiaId() { return membresiaId; }
}
