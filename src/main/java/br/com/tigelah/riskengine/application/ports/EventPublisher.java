package br.com.tigelah.riskengine.application.ports;

import br.com.tigelah.riskengine.application.events.RiskEvaluatedEvent;

public interface EventPublisher {
    void publish(RiskEvaluatedEvent event);
}
