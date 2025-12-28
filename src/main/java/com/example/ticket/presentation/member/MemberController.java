package com.example.ticket.presentation.member;

import com.example.ticket.application.member.MemberService;
import com.example.ticket.global.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")

public class MemberController {
    private final MemberService memberService;
    @PostMapping("/signup")
    public ApiResponse<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest req){
        Long id=memberService.signUp(req.email,req.password,req.name);
        return ApiResponse.ok(new SignUpResponse(id));
    }
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        String result = memberService.login(req.email, req.password);
        return ApiResponse.ok(new LoginResponse(result));
    }
    @Getter
    static class SignUpRequest {
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        public String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        public String password;

        @NotBlank(message = "이름은 필수입니다.")
        public String name;
    }
    @Getter
    @AllArgsConstructor
    static class SignUpResponse {
        private Long memberId;
    }

    @Getter
    static class LoginRequest {
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        public String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        public String password;
    }

    @Getter
    @AllArgsConstructor
    static class LoginResponse {
        private String result;
    }

}
