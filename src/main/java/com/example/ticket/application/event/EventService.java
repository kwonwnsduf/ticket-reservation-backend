package com.example.ticket.application.event;

import com.example.ticket.domain.event.Event;
import com.example.ticket.domain.event.EventRepository;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {
    private final EventRepository eventRepository;
    
    public Long create(String title, LocalDateTime startsAt,LocalDateTime endsAt){
        Event event= Event.builder().title(title).startsAt(startsAt).endsAt(endsAt).build();
        return eventRepository.save(event).getId();
    }
    @Transactional(readOnly=true)
    public Event get(Long eventId){
        return eventRepository.findById(eventId).orElseThrow(()->new ApiException(ErrorCode.NOT_FOUND));
    }
    public void update(Long eventId, String title,LocalDateTime startsAt, LocalDateTime endsAt){
        Event event=get(eventId);
        event.update(title, startsAt,endsAt);
    }
    public void delete(Long eventId){
        Event event=get(eventId);
        eventRepository.delete(event);
    }

}
