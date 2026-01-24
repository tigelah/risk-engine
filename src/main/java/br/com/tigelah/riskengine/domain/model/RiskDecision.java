package br.com.tigelah.riskengine.domain.model;

public record RiskDecision(boolean approved, String reason) {

    public static RiskDecision approve() {   // 👈 rename
        return new RiskDecision(true, "OK");
    }

    public static RiskDecision reject(String reason) { // 👈 rename
        return new RiskDecision(false, reason);
    }
}