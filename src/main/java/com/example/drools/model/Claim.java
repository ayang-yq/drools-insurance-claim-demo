package com.example.drools.model;

import lombok.Data;

@Data
public class Claim {
    private String id;
    private double amount;
    private String claimType;
    private String policyId;
    private String status = "PENDING"; // Default status
    private int riskScore;
    private java.time.LocalDate incidentDate;
    private int previousClaimsCount;
    private String comment;
    private java.util.Set<String> firedRules = new java.util.HashSet<>();
    private java.util.List<String> providedDocuments = new java.util.ArrayList<>();
}
