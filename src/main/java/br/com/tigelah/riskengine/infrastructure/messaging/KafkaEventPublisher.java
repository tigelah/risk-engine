package br.com.tigelah.riskengine.infrastructure.messaging;

import br.com.tigelah.riskengine.application.events.RiskEvaluatedEvent;
import br.com.tigelah.riskengine.application.ports.EventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisher implements EventPublisher {
    private final KafkaTemplate<String, Object> kafka;
    public KafkaEventPublisher(KafkaTemplate<String, Object> kafka) { this.kafka = kafka; }

    @Override
    public void publish(RiskEvaluatedEvent event) {
        var topic = event.approved() ? Topics.PAYMENT_RISK_APPROVED : Topics.PAYMENT_RISK_REJECTED;
        kafka.send(topic, event.paymentId().toString(), event);
    }
}