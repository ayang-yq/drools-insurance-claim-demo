package com.example.drools.service;

import com.example.drools.model.Claim;
import com.example.drools.model.Policy;
import com.example.drools.model.ClaimHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ClaimServiceTest {

    @Autowired
    private ClaimService claimService;

    @Test
    public void testInactivePolicy() {
        Claim claim = new Claim();
        claim.setId("C001");
        claim.setAmount(1000);
        claim.setPolicyId("P001");
        claim.setStatus("PENDING");

        Policy policy = new Policy();
        policy.setId("P001");
        policy.setActive(false);

        Claim processedClaim = claimService.processClaim(claim, policy);

        assertEquals("REJECTED", processedClaim.getStatus());
        assertEquals("Policy is inactive", processedClaim.getComment());
    }

    @Test
    public void testDeductibleRejection() {
        Claim claim = new Claim();
        claim.setId("C002");
        claim.setAmount(100);
        claim.setPolicyId("P002");
        claim.setStatus("PENDING");

        Policy policy = new Policy();
        policy.setId("P002");
        policy.setDeductible(500);
        policy.setLimit(10000);
        policy.setActive(true);

        Claim processedClaim = claimService.processClaim(claim, policy);

        assertEquals("REJECTED", processedClaim.getStatus());
        assertEquals("Claim amount is less than deductible", processedClaim.getComment());
    }

    @Test
    public void testLowRiskApproval() {
        Claim claim = new Claim();
        claim.setId("C003");
        claim.setAmount(1000);
        claim.setClaimType("General"); // Changed from Medical to avoid document check
        claim.setPolicyId("P003");
        claim.setStatus("PENDING");
        claim.setPreviousClaimsCount(0);

        Policy policy = new Policy();
        policy.setId("P003");
        policy.setLimit(5000);
        policy.setDeductible(100);
        policy.setActive(true);

        Claim processedClaim = claimService.processClaim(claim, policy);

        assertEquals("APPROVED", processedClaim.getStatus());
    }

    @Test
    public void testHighRiskLargeAmount() {
        Claim claim = new Claim();
        claim.setId("C004");
        claim.setAmount(15000);
        claim.setClaimType("General"); // Changed from Medical to avoid document check
        claim.setPolicyId("P004");
        claim.setStatus("PENDING");

        Policy policy = new Policy();
        policy.setId("P004");
        policy.setLimit(50000);
        policy.setDeductible(100);
        policy.setActive(true);

        Claim processedClaim = claimService.processClaim(claim, policy);

        assertEquals("INVESTIGATION_REQUIRED", processedClaim.getStatus());
        assertTrue(processedClaim.getRiskScore() >= 50);
    }

    @Test
    public void testMultipleRiskFactors() {
        // Theft (+20) + Frequent Claimant (+30) = 50 Risk -> Investigation
        Claim claim = new Claim();
        claim.setId("C005");
        claim.setAmount(2000);
        claim.setClaimType("Theft");
        // claim.setPreviousClaimsCount(5); // Removed, using history now
        claim.setPolicyId("P005");
        claim.setStatus("PENDING");

        Policy policy = new Policy();
        policy.setId("P005");
        policy.setLimit(5000);
        policy.setDeductible(100);
        policy.setActive(true);

        ClaimHistory history = new ClaimHistory();
        history.setClaimCountLast3Years(5);

        Claim processedClaim = claimService.processClaim(claim, policy, null, history);

        assertEquals("INVESTIGATION_REQUIRED", processedClaim.getStatus());
        assertTrue(processedClaim.getRiskScore() >= 50);
    }

    @Test
    public void testConflictResolution() {
        // Large amount (High Risk) BUT Policy Limit Exceeded (Reject)
        // Reject has higher salience (95) than High Risk (50)
        // Actually, both might fire, but Reject should be the final status if we handle
        // it right.
        // In my rules, Reject sets status to REJECTED.
        // High risk sets Risk Score.
        // Decision rule (Salience 10) checks if status is PENDING.
        // If Reject fired (Salience 95), status is REJECTED.
        // So Decision rule won't fire.
        // Result: REJECTED, but Risk Score might be high.

        Claim claim = new Claim();
        claim.setId("C006");
        claim.setAmount(60000); // > Limit 50000
        claim.setPolicyId("P006");
        claim.setStatus("PENDING");

        Policy policy = new Policy();
        policy.setId("P006");
        policy.setLimit(50000);
        policy.setDeductible(100);
        policy.setActive(true);

        Claim processedClaim = claimService.processClaim(claim, policy);

        assertEquals("REJECTED", processedClaim.getStatus());
        assertEquals("Policy limit exceeded", processedClaim.getComment());
        // Risk rule (Salience 50) should have fired too
        assertTrue(processedClaim.getRiskScore() >= 50);
    }
}
