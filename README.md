# Drools Insurance Demo

This project demonstrates a simple Insurance Claim handling system using **Spring Boot** and **Drools**.

## Project Structure

- **Domain Models**: `Claim`, `Policy`
- **Drools Config**: `KieConfig` loads rules from `src/main/resources/rules/insurance-rules.drl`
- **Rules**:
    1. **Validation Rules** (Highest Priority):
        - **Inactive Policy**: Rejects claims if the policy is not active.
        - **Policy Limit Exceeded**: Rejects claims if the amount exceeds the policy limit.
        - **Deductible Check**: Rejects claims if the amount is less than the deductible.
    2. **Risk Calculation Rules** (Medium Priority):
        - **Large Claims**: Adds 50 risk points for claims over $10,000.
        - **Frequent Claimant**: Adds 30 risk points if previous claims count > 3.
        - **Theft Claims**: Adds 20 risk points for "Theft" type claims.
    3. **Decision Rules** (Lowest Priority):
        - **Auto-approval**: Approves claims with Risk Score <= 20.
        - **Investigation Required**: Flags claims with Risk Score > 20 for investigation.
- **Service**: `ClaimService` executes the rules.
- **Controller**: `ClaimController` exposes a REST endpoint.

## Prerequisites

- Java 17
- Maven

## How to Run

1.  Clone the repository (or use the provided project folder).
2.  Navigate to the project directory:
    ```bash
    cd drools-insurance-demo
    ```
3.  Run the application:
    ```bash
    mvn spring-boot:run
    ```

## How to Test

You can test the rules using `curl` or any API client (like Postman).

### 1. Auto-approve Small Claim

**Request:**
```bash
curl -X POST http://localhost:8080/api/claims/process \
-H "Content-Type: application/json" \
-d '{
    "claim": {
        "id": "C001",
        "amount": 400,
        "claimType": "Medical",
        "policyId": "P001",
        "status": "PENDING"
    },
    "policy": {
        "id": "P001",
        "type": "Health",
        "limit": 5000,
        "customerId": "U001"
    }
}'
```

**Expected Response:**
Status should be `APPROVED`, Risk Score `0`.

### 2. High Risk Large Claim

**Request:**
```bash
curl -X POST http://localhost:8080/api/claims/process \
-H "Content-Type: application/json" \
-d '{
    "claim": {
        "id": "C002",
        "amount": 15000,
        "claimType": "Accident",
        "policyId": "P002",
        "status": "PENDING"
    },
    "policy": {
        "id": "P002",
        "type": "Auto",
        "limit": 20000,
        "customerId": "U002"
    }
}'
```

**Expected Response:**
Risk Score should be `100`.

### 3. Reject Limit Exceeded

**Request:**
```bash
curl -X POST http://localhost:8080/api/claims/process \
-H "Content-Type: application/json" \
-d '{
    "claim": {
        "id": "C003",
        "amount": 6000,
        "claimType": "Medical",
        "policyId": "P003",
        "status": "PENDING"
    },
    "policy": {
        "id": "P003",
        "type": "Health",
        "limit": 5000,
        "customerId": "U003"
    }
}'
```

**Expected Response:**
Status should be `REJECTED`.
