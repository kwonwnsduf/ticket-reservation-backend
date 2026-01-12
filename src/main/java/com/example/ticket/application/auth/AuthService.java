package com.example.ticket.application.auth;

import com.example.ticket.domain.member.Member;
import com.example.ticket.domain.member.MemberRepository;
import com.example.ticket.domain.member.Role;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import com.example.ticket.global.security.JwtProvider;
import com.example.ticket.presentation.auth.dto.LoginRequest;
import com.example.ticket.presentation.auth.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private  final JwtProvider jwtProvider;
    public void signup(SignupRequest req){
        if(memberRepository.findByEmail(req.getEmail()).isPresent()){
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL);
        }
        String encoded=passwordEncoder.encode(req.getPassword());
        Member member= Member.builder().email(req.getEmail()).password(encoded).role(Role.USER).build();
        memberRepository.save(member);
    }
    @Transactional(readOnly = true)
    public String login(LoginRequest req) {
        Member member = memberRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD);
        }

        return jwtProvider.createToken(member.getId(), member.getRole());
    }
}
