package br.com.tigelah.riskengine.infrastructure.config;

import br.com.tigelah.riskengine.application.usecase.AnalyzeRiskUseCase;
import br.com.tigelah.riskengine.domain.services.SimpleRiskRules;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class UseCaseConfig {
    @Bean
    public SimpleRiskRules simpleRiskRules(
            @Value("${risk.max-amount-cents:500000}") long maxAmountCents,
            @Value("${risk.blocked-last4:0000,6666}") String blockedLast4
    ) {
        return new SimpleRiskRules(maxAmountCents, Set.of(blockedLast4.split(",")));
    }

    @Bean
    public AnalyzeRiskUseCase analyzeRiskUseCase(SimpleRiskRules rules) {
        return new AnalyzeRiskUseCase(rules);
    }
}