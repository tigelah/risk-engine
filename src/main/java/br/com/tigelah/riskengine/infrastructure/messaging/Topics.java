package br.com.tigelah.riskengine.infrastructure.messaging;

public final class Topics {
    private Topics() {}
    public static final String PAYMENT_AUTHORIZE_REQUESTED = "payment.authorize.requested";
    public static final String PAYMENT_RISK_APPROVED = "payment.risk.approved";
    public static final String PAYMENT_RISK_REJECTED = "payment.risk.rejected";
}
