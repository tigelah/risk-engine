package br.com.tigelah.riskengine.entrypoints.kafka;

import br.com.tigelah.riskengine.application.events.RiskEvaluatedEvent;
import br.com.tigelah.riskengine.application.ports.EventPublisher;
import br.com.tigelah.riskengine.application.usecase.AnalyzeRiskUseCase;
import br.com.tigelah.riskengine.entrypoints.kafka.dto.PaymentAuthorizeRequestedEvent;
import br.com.tigelah.riskengine.infrastructure.messaging.Topics;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Component
public class RiskEventsConsumer {
    private static final Logger log = LoggerFactory.getLogger(RiskEventsConsumer.class);

    private final ObjectMapper mapper;
    private final AnalyzeRiskUseCase useCase;
    private final EventPublisher publisher;
    private final Clock clock;

    public RiskEventsConsumer(ObjectMapper mapper, AnalyzeRiskUseCase useCase, EventPublisher publisher, Clock clock) {
        this.mapper = mapper;
        this.useCase = useCase;
        this.publisher = publisher;
        this.clock = clock;
    }

    @KafkaListener(topics = Topics.PAYMENT_AUTHORIZE_REQUESTED, groupId = "${kafka.consumer.group-id:risk-engine}")
    public void onMessage(String message) {
        try {
            var evt = mapper.readValue(message, PaymentAuthorizeRequestedEvent.class);
            if (evt.correlationId() != null) MDC.put("correlationId", evt.correlationId());

            var decision = useCase.execute(evt.amountCents(), evt.panLast4());

            var out = new RiskEvaluatedEvent(
                    UUID.randomUUID(),
                    Instant.now(clock),
                    evt.correlationId(),
                    decision.approved() ? Topics.PAYMENT_RISK_APPROVED : Topics.PAYMENT_RISK_REJECTED,
                    evt.paymentId(),
                    decision.approved(),
                    decision.reason()
            );

            publisher.publish(out);
            log.info("risk_evaluated paymentId={} approved={} reason={}", evt.paymentId(), decision.approved(), decision.reason());
        } catch (Exception e) {
            log.error("Failed to consume message: {}", message, e);
        } finally {
            MDC.clear();
        }
    }
}