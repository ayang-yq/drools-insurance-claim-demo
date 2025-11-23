package com.example.drools.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Discrepancy {
    private String fieldName; // e.g., "amount", "date"
    private String submittedValue; // e.g., "1000"
    private String evidenceValue; // e.g., "1200" (from OCR)
    private double confidence; // e.g., 0.95 (OCR confidence)
}
