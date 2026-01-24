package br.com.tigelah.riskengine.entrypoints.kafka.dto;

import java.time.Instant;
import java.util.UUID;

public record PaymentAuthorizeRequestedEvent(
        UUID eventId,
        Instant occurredAt,
        String correlationId,
        String type,
        UUID paymentId,
        long amountCents,
        String panLast4
) { }

