package com.example.drools.controller;

import com.example.drools.model.Claim;
import com.example.drools.model.Policy;
import com.example.drools.service.ClaimService;
import com.example.drools.model.ClaimHistory;
import com.example.drools.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    @Autowired
    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/process")
    public Claim processClaim(@RequestBody ClaimRequest request) {
        return claimService.processClaim(request.getClaim(), request.getPolicy(), request.getDocuments(),
                request.getClaimHistory());
    }

    // Helper class for request body
    @lombok.Data
    public static class ClaimRequest {
        private Claim claim;
        private Policy policy;
        private java.util.List<Document> documents;
        private ClaimHistory claimHistory;
    }
}
