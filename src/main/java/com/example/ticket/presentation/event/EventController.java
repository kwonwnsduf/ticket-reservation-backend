package com.example.ticket.presentation.event;

import com.example.ticket.application.event.EventService;
import com.example.ticket.presentation.event.dto.CreateEventRequest;
import com.example.ticket.presentation.event.dto.CreateEventResponse;
import com.example.ticket.presentation.event.dto.EventResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    @PostMapping
    public ResponseEntity<CreateEventResponse> create(@RequestBody @Valid CreateEventRequest req){
        Long id= eventService.create(req.getTitle(),req.getStartsAt(),req.getStartsAt());
        return ResponseEntity.created(URI.create("/api/events/"+id)).body(new CreateEventResponse(id));
    }
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> get(@PathVariable Long eventId)
    {
        var event=eventService.get(eventId);
        return ResponseEntity.ok(new EventResponse(event.getId(), event.getTitle(), event.getStartsAt(),event.getEndsAt()));
    }
    @PutMapping("/{eventId}")
    public ResponseEntity<Void> update(@PathVariable Long eventId, @RequestBody @Valid CreateEventRequest req) {
        eventService.update(eventId, req.getTitle(), req.getStartsAt(), req.getStartsAt());
        return ResponseEntity.noContent().build(); // 204
    }
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(@PathVariable Long eventId){
        eventService.delete(eventId);
        return ResponseEntity.noContent().build();
    }

}
