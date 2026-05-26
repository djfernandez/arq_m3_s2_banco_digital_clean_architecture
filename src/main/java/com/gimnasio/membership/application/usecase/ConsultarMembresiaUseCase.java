package com.gimnasio.membership.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gimnasio.membership.domain.exception.MembresiaNotFoundException;
import com.gimnasio.membership.domain.model.Membresia;
import com.gimnasio.membership.domain.repository.MembresiaRepository;
import com.gimnasio.notifications.NotificationPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CASO DE USO: Consultar Membresía
 * Evento generado: MembresiaConsultada
 * Alerta: NotificacionEnviada si vence en 5 días
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultarMembresiaUseCase {

    private static final int DIAS_ALERTA_VENCIMIENTO = 5;

    private final MembresiaRepository membresiaRepository;
    private final NotificationPort notificationPort;

    @Transactional(readOnly = true)
    public Membresia execute(String membresiaId) {
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new MembresiaNotFoundException(membresiaId));

        log.info("Evento: MembresiaConsultada — {} estado:{}", membresiaId, membresia.getEstado());

        // Alerta de vencimiento próximo (requisito 4)
        if (membresia.venceEnDias(DIAS_ALERTA_VENCIMIENTO)) {
            notificationPort.notificarVencimientoProximo(membresia.getSocioId(), membresia.getFechaFin());
        }

        return membresia;
    }

    @Transactional(readOnly = true)
    public List<Membresia> findBySocio(String socioId) {
        return membresiaRepository.findBySocioId(socioId);
    }
}
