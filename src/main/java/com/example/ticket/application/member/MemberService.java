package com.example.ticket.application.member;

import com.example.ticket.domain.member.Member;
import com.example.ticket.domain.member.MemberRepository;
import com.example.ticket.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    public Long signUp(String email, String password, String name){
        if(memberRepository.existsByEmail(email)){
            throw new ApiException(409, "이미 사용중인 이메일입니다.");
        }
        Member member=Member.builder().email(email).password(password).name(name).build();
        return memberRepository.save(member).getId();
    }
    @Transactional(readOnly=true)
    public String login(String email, String password){
        Member member=memberRepository.findByEmail(email).orElseThrow(()->new ApiException(401, "이메일 또는 비밀번호가 올바르지 않습니다."));

    if(!member.getPassword().equals(password)){
        throw new ApiException(401, "이메일 또는 비밀번호가 올바르지 않습니다");
    }
    return "LOGIN_OK";
}}
