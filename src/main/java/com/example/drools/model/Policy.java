package com.example.drools.model;

import lombok.Data;

@Data
public class Policy {
    private String id;
    private String type;
    private double limit;
    private double deductible;
    private boolean active = true;
    private String customerId;
}
