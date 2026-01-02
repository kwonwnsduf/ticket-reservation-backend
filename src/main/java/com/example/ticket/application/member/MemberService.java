package com.example.ticket.application.member;

import com.example.ticket.domain.member.Member;
import com.example.ticket.domain.member.MemberRepository;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    public Long signUp(String email, String password){
        if(memberRepository.existsByEmail(email)){
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL);
        }
        Member member=Member.builder().email(email).password(password).build();
        return memberRepository.save(member).getId();
    }
    @Transactional(readOnly=true)
    public Long login(String email, String password){
        Member member=memberRepository.findByEmail(email).orElseThrow(()->new ApiException(ErrorCode.MEMBER_NOT_FOUND));

    if(!member.matchPassword(password)){
        throw new ApiException(ErrorCode.INVALID_PASSWORD);
    }
    return member.getId();
}}
