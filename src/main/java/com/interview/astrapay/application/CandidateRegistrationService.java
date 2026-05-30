package com.interview.astrapay.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.interview.astrapay.presentation.dto.request.CandidateRegistrationRequest;
import com.interview.astrapay.presentation.dto.result.CandidateRegistrationResult;

@Service
public class CandidateRegistrationService {
    public CandidateRegistrationResult register (CandidateRegistrationRequest registrationRequest) {
        validateRequest(registrationRequest);
        return new CandidateRegistrationResult(
            generateUUID(),
            registrationRequest.getEmail()
        );
    }

    private UUID generateUUID () {
        return UUID.randomUUID();
    }

    private void validateRequest(CandidateRegistrationRequest candidateRegistrationRequest) {
        if (candidateRegistrationRequest.getEmail() == null) {
            throw new IllegalArgumentException("Email is required");
        }
        if (candidateRegistrationRequest.getFullName() == null) {
            throw new IllegalArgumentException("FullName is required");
        }
    }
}
