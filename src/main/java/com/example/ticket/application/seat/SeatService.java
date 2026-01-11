package com.example.ticket.application.seat;

import com.example.ticket.application.event.EventService;
import com.example.ticket.domain.event.Event;
import com.example.ticket.domain.hold.HoldStore;
import com.example.ticket.domain.seat.Seat;
import com.example.ticket.domain.seat.SeatRepository;
import com.example.ticket.domain.seat.SeatStatus;
import com.example.ticket.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatService {

    private final SeatRepository seatRepository;
    private final EventService eventService;
    private final HoldStore holdStore;

    public Long create(Long eventId, String seatNo) {

        Event event = eventService.get(eventId);
        Seat seat = Seat.builder()
                .event(event)
                .seatNo(seatNo)
                .build();
        return seatRepository.save(seat).getId();
    }

    @Transactional(readOnly = true)
    public List<Seat> list(Long eventId) {
        return seatRepository.findByEventId(eventId);
    }
    public String displayStatus(Long eventId, Seat seat) {
        if (seat.getStatus() == SeatStatus.OCCUPIED) return "OCCUPIED";
        // Redis에 hold가 있으면 HELD처럼 보여주기
        return (holdStore.getHolder(eventId, seat.getId()) != null) ? "HELD" : "AVAILABLE";
    }
}
