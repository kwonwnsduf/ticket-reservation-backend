package com.example.ticket.domain.hold;

import java.time.Duration;

public interface HoldStore {
    boolean tryHold(Long eventId,Long seatId, Long memberId, Duration ttl);
    Long getHolder(Long eventId,Long seatId);
    void release(Long eventId,Long seatId);
}
