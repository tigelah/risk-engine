package br.com.tigelah.riskengine.entrypoints.kafka;

import br.com.tigelah.riskengine.application.usecase.AnalyzeRiskUseCase;
import br.com.tigelah.riskengine.application.events.RiskEvaluatedEvent;
import br.com.tigelah.riskengine.application.ports.EventPublisher;
import br.com.tigelah.riskengine.infrastructure.messaging.Topics;
import com.fasterxml.jackson.databind.JsonNode;
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

    @KafkaListener(
            topics = Topics.PAYMENT_AUTHORIZE_REQUESTED,
            groupId = "${kafka.consumer.group-id:risk-engine}"
    )
    public void onMessage(String message) {
        try {
            JsonNode root = mapper.readTree(message);

            String type = root.path("type").asText("");
            if (!Topics.PAYMENT_AUTHORIZE_REQUESTED.equals(type)) {
                log.debug("Ignoring event type={}", type);
                return;
            }

            String correlationId = root.path("correlationId").asText(null);
            if (correlationId != null) MDC.put("correlationId", correlationId);

            UUID paymentId = UUID.fromString(root.path("paymentId").asText());

            long amountCents = root.path("amountCents").asLong(0);
            int installments = root.path("installments").asInt(1);

            String panLast4 = root.path("panLast4").asText(null);
            String accountId = root.path("accountId").asText(null);
            String userId = root.path("userId").asText(null);
            String panHash = root.path("panHash").asText(null);

            var decision = useCase.execute(amountCents, panLast4);

            String outType = decision.approved()
                    ? Topics.PAYMENT_RISK_APPROVED
                    : Topics.PAYMENT_RISK_REJECTED;

            var out = new RiskEvaluatedEvent(
                    UUID.randomUUID(),
                    Instant.now(clock),
                    correlationId,
                    outType,
                    paymentId,
                    decision.approved(),
                    decision.reason(),
                    amountCents,
                    installments,
                    accountId,
                    userId,
                    panHash,
                    panLast4
            );

            publisher.publish(out);
            log.info("risk_evaluated paymentId={} approved={} reason={} installments={}",
                    paymentId, decision.approved(), decision.reason(), installments);

        } catch (Exception e) {
            log.error("Failed to consume risk message: {}", message, e);
        } finally {
            MDC.clear();
        }
    }
}