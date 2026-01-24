# risk-engine

O **risk-engine** simula um **motor de risco antifraude** no fluxo de adquirência.
Ele avalia transações e decide se devem ser **aprovadas ou rejeitadas por risco**.

## Propósito
Executar análise antifraude de forma **assíncrona**, desacoplada do acquirer-core.

## Comunicação
- Consome: `payment.authorize.requested`
- Produz:
    - `payment.risk.approved`
    - `payment.risk.rejected`

## Arquitetura (Clean)
- domain: regras de risco
- application: `AnalyzeRiskUseCase`
- entrypoints: consumer Kafka
- infrastructure: publisher Kafka

## Papel no fluxo
acquirer-core → risk-engine → acquirer-core


## Rodar
```bash
mvn clean spring-boot:run
```

## Testes + cobertura
```bash
mvn clean verify
```