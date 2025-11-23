package com.example.drools.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimHistory {
    private String customerId;
    private int claimCountLast3Years;
    private double totalClaimAmountLast3Years;
    private List<String> previousClaimTypes;
}
