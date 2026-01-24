package br.com.tigelah.riskengine.domain.services;

import br.com.tigelah.riskengine.domain.model.RiskDecision;

import java.util.Set;

public final class SimpleRiskRules {
    private final long maxAmountCents;
    private final Set<String> blockedLast4;

    public SimpleRiskRules(long maxAmountCents, Set<String> blockedLast4) {
        this.maxAmountCents = maxAmountCents;
        this.blockedLast4 = blockedLast4;
    }

    public RiskDecision evaluate(long amountCents, String panLast4) {
        if (amountCents <= 0) return RiskDecision.reject("amount_invalid");
        if (amountCents > maxAmountCents) return RiskDecision.reject("amount_too_high");
        if (panLast4 != null && blockedLast4.contains(panLast4)) return RiskDecision.reject("pan_blocked");
        return RiskDecision.approve();
    }
}