package com.interview.astrapay.infrastructure.dto.request;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // bikin constructor kosong
@AllArgsConstructor // bikin constructor dengan semua field agar bisa dinput
public class CandidateRegistrationRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Email
    private String fullName;
}
