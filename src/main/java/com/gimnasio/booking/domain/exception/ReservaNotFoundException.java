package com.gimnasio.booking.domain.exception;

public class ReservaNotFoundException extends RuntimeException {
    private final String reservaId;
    public ReservaNotFoundException(String reservaId) {
        super("Reserva no encontrada: " + reservaId);
        this.reservaId = reservaId;
    }
    public String getReservaId() { return reservaId; }
}
