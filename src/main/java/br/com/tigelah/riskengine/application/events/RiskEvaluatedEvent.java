package br.com.tigelah.riskengine.application.events;

import java.time.Instant;
import java.util.UUID;

public record RiskEvaluatedEvent(
        UUID eventId,
        Instant occurredAt,
        String correlationId,
        String type,
        UUID paymentId,
        boolean approved,
        String reason,
        long amountCents,
        Integer installments,
        String accountId,
        String userId,
        String panHash,
        String panLast4
) { }
