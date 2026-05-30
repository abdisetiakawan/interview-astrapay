package com.interview.astrapay.presentation.dto.result;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@NoArgsConstructor
@AllArgsConstructor // bikin object response lgsg dengan semua field tanpa nulis constructor
public class CandidateRegistrationResult {
    private UUID candidateId;
    private String email;
}
