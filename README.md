# Drools Insurance Claim Demo

This repository demonstrates a small insurance claim processing demo built with Spring Boot and Drools (KIE). It shows how business rules can be used to validate claims, calculate risk, and decide approval vs investigation vs rejection.

## Highlights

- Spring Boot application that loads Drools rules from `src/main/resources/rules/insurance-rules.drl` via `KieConfig`.
- Main service is `ClaimService` which creates a KIE session, inserts facts (Claim, Policy, ClaimHistory, Discrepancy, Documents) and fires rules.
- A lightweight `DocumentVerificationService` simulates OCR-based verification and produces `Discrepancy` facts for the rules to act upon.
- REST endpoint: POST `/api/claims/process` handled by `ClaimController`.

## Tech stack

- Java 17
- Spring Boot 3.x
- Drools / KIE (configured to use Drools version defined in `pom.xml`)
- Maven

## Project layout (important files)

- `src/main/java/com/example/drools/DroolsDemoApplication.java` — Spring Boot entry point
- `src/main/java/com/example/drools/config/KieConfig.java` — KIE container initialization
- `src/main/java/com/example/drools/controller/ClaimController.java` — REST controller with `/api/claims/process`
- `src/main/java/com/example/drools/service/ClaimService.java` — orchestrates Drools session and rule execution
- `src/main/java/com/example/drools/service/DocumentVerificationService.java` — mock OCR verification
- `src/main/resources/rules/insurance-rules.drl` — Drools rules
- `src/main/java/com/example/drools/model/*` — domain models: `Claim`, `Policy`, `ClaimHistory`, `Document`, `Discrepancy`
- `src/test/java/.../ClaimServiceTest.java` — unit tests covering common scenarios

## Models (summary)

- Claim (fields used): `id`, `amount`, `claimType`, `policyId`, `status` (default "PENDING"), `riskScore`, `comment`, `providedDocuments`, `firedRules`, `previousClaimsCount`, `incidentDate`.
- Policy: `id`, `type`, `limit`, `deductible`, `active`.

## How to build

You need Java 17 and Maven installed.

From project root:

```powershell
mvn clean package
```

To run the app:

```powershell
mvn spring-boot:run
```

Alternatively run the generated jar:

```powershell
java -jar target\drools-insurance-claim-demo-0.0.1-SNAPSHOT.jar
```

## API: process claim

POST /api/claims/process

Content-Type: application/json

Request body shape (JSON):

{
  "claim": { ... },
  "policy": { ... },
  "documents": [ ... ],         // optional
  "claimHistory": { ... }      // optional
}

Minimal example (curl / PowerShell):

```powershell
curl -X POST "http://localhost:8080/api/claims/process" -H "Content-Type: application/json" -d '{
  "claim": {
    "id": "C001",
    "amount": 400,
    "claimType": "General",
    "policyId": "P001",
    "status": "PENDING"
  },
  "policy": {
    "id": "P001",
    "type": "Health",
    "limit": 5000,
    "deductible": 100,
    "active": true
  }
}'
```

Typical responses: the returned `Claim` object will have updated `status` (e.g. `APPROVED`, `REJECTED`, `INVESTIGATION_REQUIRED`), `comment` and `riskScore` depending on rule evaluation.

## Rules overview

Rules are implemented in `insurance-rules.drl`. Key groups:

- Validation rules (high salience): reject when policy inactive, limit exceeded, missing required documents, or amount < deductible.
- Discrepancy handling: when a `Discrepancy` fact exists, the claim is flagged for investigation.
- Risk calculation (medium salience): rules add to `riskScore` for large claims, theft claims, frequent claimants, and suspicious keywords.
- Decision rules (low salience): auto-approve low risk (<= 20) or require investigation when risk > 20.

Salience controls order and lets validation/rejection take precedence over decision rules.

## Tests

Unit tests are under `src/test/java` and cover scenarios such as inactive policy rejection, deductible rejection, low-risk approval, high-risk investigation, multiple risk factors, and conflict resolution.

Run tests with:

```powershell
mvn test
```

The project currently includes a test class `ClaimServiceTest` exercising the main rule flows.

## Extending the demo

- Add or modify rules in `src/main/resources/rules/insurance-rules.drl`. KIE will pick them up on build/start.
- Implement a real `DocumentVerificationService` to call an OCR provider and convert results into `Discrepancy` facts.
- Add integration tests that call the REST endpoint and assert end-to-end behavior.

## Notes and gotchas

- The `DocumentVerificationService` is a mock and should be replaced for production.
- Rules rely on certain field names (see models). Changing model property names requires updating DRL accordingly.
- Drools uses object equality semantics in the DRL; ensure objects are correctly inserted into the KIE session.

## Contact / License

This is a demo repository. See project root for license (if any) or add one as needed.

---

If you want, I can also add a short example Postman collection or expand the README with generated example responses.
