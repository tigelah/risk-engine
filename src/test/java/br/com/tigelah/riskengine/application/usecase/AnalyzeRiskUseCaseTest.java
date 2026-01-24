package br.com.tigelah.riskengine.application.usecase;

import br.com.tigelah.riskengine.domain.services.SimpleRiskRules;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AnalyzeRiskUseCaseTest {
    @Test
    void delegates() {
        var uc = new AnalyzeRiskUseCase(new SimpleRiskRules(1000, Set.of("0000")));
        assertTrue(uc.execute(1, "1111").approved());
    }
}
