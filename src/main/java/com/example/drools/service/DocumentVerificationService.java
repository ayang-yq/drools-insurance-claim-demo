package com.example.drools.service;

import com.example.drools.model.Claim;
import com.example.drools.model.Discrepancy;
import com.example.drools.model.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentVerificationService {

    /**
     * Simulates an OCR and verification process.
     * In a real system, this would call an external OCR service (like AWS Textract
     * or Google Cloud Vision),
     * parse the results, and compare them against the Claim object.
     */
    public List<Discrepancy> verifyDocuments(Claim claim, List<com.example.drools.model.Document> documents) {
        List<Discrepancy> discrepancies = new ArrayList<>();

        if (documents == null || documents.isEmpty()) {
            return discrepancies;
        }

        Double totalExtractedAmount = documents.stream()
                .filter(doc -> "Invoice".equalsIgnoreCase(doc.getType()))
                .mapToDouble(doc -> doc.getExtractedAmount() != null ? doc.getExtractedAmount() : 0)
                .sum();

        if (claim.getClaimType() == "Medical" && totalExtractedAmount != null
                && Math.abs(totalExtractedAmount - claim.getAmount()) > 1.0) {
            discrepancies.add(new Discrepancy(
                    "amount",
                    String.valueOf(claim.getAmount()),
                    String.valueOf(totalExtractedAmount),
                    1.0));
        }
        for (Document doc : documents) {
            // Check Date Mismatch (Mock logic for now, assuming extractedDate is
            // YYYY-MM-DD)
            if (doc.getExtractedDate() != null && claim.getIncidentDate() != null) {
                if (!doc.getExtractedDate().equals(claim.getIncidentDate().toString())) {
                    discrepancies.add(new Discrepancy(
                            "incidentDate",
                            String.valueOf(claim.getIncidentDate()),
                            doc.getExtractedDate(),
                            doc.getConfidenceScore()));
                }
            }
        }

        return discrepancies;
    }
}
