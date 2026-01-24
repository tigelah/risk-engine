package br.com.tigelah.riskengine.entrypoints.kafka;

import br.com.tigelah.riskengine.application.events.RiskEvaluatedEvent;
import br.com.tigelah.riskengine.application.ports.EventPublisher;
import br.com.tigelah.riskengine.application.usecase.AnalyzeRiskUseCase;
import br.com.tigelah.riskengine.domain.services.SimpleRiskRules;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RiskEventsConsumerTest {

    @Test
    void publishes_rejected() {
        var mapper = new ObjectMapper().findAndRegisterModules();
        var uc = new AnalyzeRiskUseCase(new SimpleRiskRules(10, Set.of("0000")));
        var outRef = new AtomicReference<RiskEvaluatedEvent>();
        EventPublisher publisher = outRef::set;

        var clock = Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC);
        var consumer = new RiskEventsConsumer(mapper, uc, publisher, clock);

        var msg = String.format("{\"eventId\":\"%s\",\"occurredAt\":\"2030-01-01T00:00:00Z\",\"correlationId\":\"c1\",\"type\":\"payment.authorize.requested\",\"paymentId\":\"%s\",\"amountCents\":11,\"panLast4\":\"1111\"}",
                UUID.randomUUID(), UUID.randomUUID());

        consumer.onMessage(msg);

        assertNotNull(outRef.get());
        assertFalse(outRef.get().approved());
    }
}
