package com.interview.astrapay.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.interview.astrapay.infrastructure.dto.request.CandidateRegistrationRequest;
import com.interview.astrapay.infrastructure.dto.result.CandidateRegistrationResult;

@Service
public class CandidateRegistrationService {
    public CandidateRegistrationResult register (CandidateRegistrationRequest registrationRequest) {
        return new CandidateRegistrationResult(
            generateUUID(),
            registrationRequest.getEmail()
        );
    }

    private UUID generateUUID () {
        return UUID.randomUUID();
    }
}
