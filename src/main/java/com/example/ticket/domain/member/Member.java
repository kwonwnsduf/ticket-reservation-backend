package com.example.ticket.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="members")

public class Member {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique=true)
    private String email;
    @Column(nullable = false)
    private String password;
    
    @Builder
    private Member(String email, String password){
        this.email=email;
        this.password=password;
        
    }
    public boolean matchPassword(String raw){
        return this.password.equals(raw);
    }
}
