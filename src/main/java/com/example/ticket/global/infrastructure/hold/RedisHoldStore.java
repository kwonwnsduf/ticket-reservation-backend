package com.example.ticket.global.infrastructure.hold;

import com.example.ticket.domain.hold.HoldStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisHoldStore implements HoldStore {
    private final StringRedisTemplate redis;
    private String key(Long eventId, Long seatId){
        return "ticket:hold:%d:%d".formatted(eventId,seatId);
    }

    @Override
    public boolean tryHold(Long eventId, Long seatId, Long memberId, Duration ttl) {
        // SETNX + TTL
        Boolean ok = redis.opsForValue().setIfAbsent(key(eventId, seatId), String.valueOf(memberId), ttl);
        return Boolean.TRUE.equals(ok);
    }

    @Override
    public Long getHolder(Long eventId, Long seatId) {
        String v = redis.opsForValue().get(key(eventId, seatId));
        return (v == null) ? null : Long.valueOf(v);
    }

    @Override
    public void release(Long eventId, Long seatId) {
        redis.delete(key(eventId, seatId));
    }
}
