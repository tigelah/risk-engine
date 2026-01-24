package br.com.tigelah.riskengine.application.usecase;

import br.com.tigelah.riskengine.domain.model.RiskDecision;
import br.com.tigelah.riskengine.domain.services.SimpleRiskRules;

public class AnalyzeRiskUseCase {
    private final SimpleRiskRules rules;
    public AnalyzeRiskUseCase(SimpleRiskRules rules) { this.rules = rules; }
    public RiskDecision execute(long amountCents, String panLast4) { return rules.evaluate(amountCents, panLast4); }
}