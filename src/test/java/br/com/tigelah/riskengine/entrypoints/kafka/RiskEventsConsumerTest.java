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
    void publishes_approved_and_propagates_installments_and_context() {
        var mapper = new ObjectMapper().findAndRegisterModules();
        var uc = new AnalyzeRiskUseCase(new SimpleRiskRules(9999, Set.of())); // aprova
        var outRef = new AtomicReference<RiskEvaluatedEvent>();
        EventPublisher publisher = outRef::set;

        var clock = Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC);
        var consumer = new RiskEventsConsumer(mapper, uc, publisher, clock);

        var paymentId = UUID.randomUUID();
        var accountId = UUID.randomUUID();

        var msg = """
          {
            "eventId":"%s",
            "occurredAt":"2030-01-01T00:00:00Z",
            "correlationId":"c1",
            "type":"payment.authorize.requested",
            "paymentId":"%s",
            "amountCents":100,
            "currency":"BRL",
            "installments":6,
            "accountId":"%s",
            "userId":"user-1",
            "panHash":"h1",
            "panLast4":"1111"
          }
        """.formatted(UUID.randomUUID(), paymentId, accountId);

        consumer.onMessage(msg);

        assertNotNull(outRef.get());
        assertTrue(outRef.get().approved());
        assertEquals(6, outRef.get().installments());
        assertEquals(accountId.toString(), outRef.get().accountId());
        assertEquals("user-1", outRef.get().userId());
        assertEquals("h1", outRef.get().panHash());
        assertEquals("1111", outRef.get().panLast4());
        assertEquals(100L, outRef.get().amountCents());
    }
}