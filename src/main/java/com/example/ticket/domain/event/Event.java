package com.example.ticket.domain.event;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Entity
@Table(name="events")
public class Event {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private String title;
    @Column(nullable=false)
    private LocalDateTime startsAt;
    @Column(nullable = false)
    private LocalDateTime endsAt;
    @Builder
    private Event(String title, LocalDateTime startsAt, LocalDateTime endsAt){
        this.title=title;
        this.startsAt=startsAt;
        this.endsAt=endsAt;
    }
    public void update(String title,LocalDateTime startsAt,LocalDateTime endsAt){
        this.title=title;
        this.startsAt=startsAt;
        this.endsAt=endsAt;
    }

}

