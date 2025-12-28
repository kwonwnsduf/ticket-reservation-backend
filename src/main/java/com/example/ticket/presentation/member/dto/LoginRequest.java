package com.example.ticket.presentation.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    public String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    public String password;
}
