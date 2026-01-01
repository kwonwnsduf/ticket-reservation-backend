package com.example.ticket.presentation.member;

import com.example.ticket.application.member.MemberService;
import com.example.ticket.global.dto.ApiResponse;
import com.example.ticket.presentation.member.dto.LoginRequest;
import com.example.ticket.presentation.member.dto.SignUpRequest;
import com.example.ticket.presentation.member.dto.SignUpResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest req){
        Long id=memberService.signUp(req.getEmail(),req.getPassword());
        return ResponseEntity.ok(new SignUpResponse(id));
    }
    @PostMapping("/login")
    public ResponseEntity<SignUpResponse> login(@RequestBody @Valid LoginRequest req) {
        Long id = memberService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(new SignUpResponse(id));
    }

}
