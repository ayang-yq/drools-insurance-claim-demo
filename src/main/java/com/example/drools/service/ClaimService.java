package com.example.drools.service;

import com.example.drools.model.Claim;
import com.example.drools.model.Policy;
import com.example.drools.model.ClaimHistory;
import com.example.drools.model.Document;
import com.example.drools.model.Discrepancy;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClaimService {

    private final KieContainer kieContainer;
    private final DocumentVerificationService verificationService;

    @Autowired
    public ClaimService(KieContainer kieContainer, DocumentVerificationService verificationService) {
        this.kieContainer = kieContainer;
        this.verificationService = verificationService;
    }

    public Claim processClaim(Claim claim, Policy policy) {
        return processClaim(claim, policy, null, null);
    }

    public Claim processClaim(Claim claim, Policy policy, java.util.List<Document> documents,
            ClaimHistory history) {
        KieSession kieSession = kieContainer.newKieSession();

        // 1. Verification Step (Mock OCR)
        java.util.List<Discrepancy> discrepancies = verificationService.verifyDocuments(claim,
                documents);

        // Populate provided documents for rules to check
        if (documents != null) {
            for (Document doc : documents) {
                claim.getProvidedDocuments().add(doc.getType());
            }
        }

        // Insert facts into the session
        kieSession.insert(claim);
        if (policy != null) {
            kieSession.insert(policy);
        }
        if (history != null) {
            kieSession.insert(history);
        }

        // Insert discrepancies
        for (Discrepancy discrepancy : discrepancies) {
            kieSession.insert(discrepancy);
        }

        // Fire all rules
        kieSession.fireAllRules();

        // Dispose the session to free resources
        kieSession.dispose();

        return claim;
    }
}
