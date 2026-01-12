package com.example.ticket.presentation.member;

import com.example.ticket.application.member.MemberService;
import com.example.ticket.domain.member.Member;
import com.example.ticket.domain.member.MemberRepository;
import com.example.ticket.global.dto.ApiResponse;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import com.example.ticket.presentation.member.dto.LoginRequest;
import com.example.ticket.presentation.member.dto.MemberMeResponse;
import com.example.ticket.presentation.member.dto.SignUpRequest;
import com.example.ticket.presentation.member.dto.SignUpResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")

public class MemberController {
//   // private final MemberService memberService;
//    @PostMapping("/signup")
//    public ResponseEntity<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest req){
//        Long id=memberService.signUp(req.getEmail(),req.getPassword());
//        return ResponseEntity.ok(new SignUpResponse(id));
//    }
//    @PostMapping("/login")
//    public ResponseEntity<SignUpResponse> login(@RequestBody @Valid LoginRequest req) {
//        Long id = memberService.login(req.getEmail(), req.getPassword());
//        return ResponseEntity.ok(new SignUpResponse(id));
//   }
private final MemberRepository memberRepository;

    private Long currentMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal(); // JwtFilter에서 memberId 넣어둔 값
    }
    @GetMapping("/me")
    public ResponseEntity<MemberMeResponse> me() {
        Long memberId = currentMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        return ResponseEntity.ok(new MemberMeResponse(member.getId(), member.getEmail()));
    }

}
