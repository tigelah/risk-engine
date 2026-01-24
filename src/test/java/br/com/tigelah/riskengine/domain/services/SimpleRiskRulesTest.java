package br.com.tigelah.riskengine.domain.services;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SimpleRiskRulesTest {
    @Test
    void approve_ok() {
        var rules = new SimpleRiskRules(1000, Set.of("0000"));
        assertTrue(rules.evaluate(10, "1111").approved());
    }

    @Test
    void reject_over_limit() {
        var rules = new SimpleRiskRules(10, Set.of("0000"));
        var d = rules.evaluate(11, "1111");
        assertFalse(d.approved());
        assertEquals("amount_too_high", d.reason());
    }
}

