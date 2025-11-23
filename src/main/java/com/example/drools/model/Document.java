package com.example.drools.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    private String id;
    private String type; // e.g., "Invoice", "TreatmentRecord", "PoliceReport"
    private String ocrText;
    private LocalDateTime uploadedAt;
    private double confidenceScore;
    private Double extractedAmount; // Nullable, only for Invoices
    private String extractedDate; // Extracted date string
}
