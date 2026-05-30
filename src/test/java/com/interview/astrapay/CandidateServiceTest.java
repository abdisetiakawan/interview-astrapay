package com.interview.astrapay;

import org.junit.Test;

import com.interview.astrapay.application.CandidateRegistrationService;
import com.interview.astrapay.infrastructure.dto.request.CandidateRegistrationRequest;
import com.interview.astrapay.infrastructure.dto.result.CandidateRegistrationResult;
import static org.assertj.core.api.Assertions.assertThat;

public class CandidateServiceTest {
    private final CandidateRegistrationService candidateService = new CandidateRegistrationService();
    @Test
    public void shouldRegisterCandidateSuccessfully() {
        // given
        CandidateRegistrationRequest request = new CandidateRegistrationRequest("abdisetiawan.dev@gmail.com", "Abdi Setiawan");
        // when
        CandidateRegistrationResult result = candidateService.register(request);
        // then
        assertThat(result.getCandidateId()).isNotNull();
        assertThat(result.getEmail()).isEqualTo("abdisetiawan.dev@gmail.com");
    }
}
