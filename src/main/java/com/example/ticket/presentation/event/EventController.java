package com.example.ticket.presentation.event;

import com.example.ticket.application.event.EventService;
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
    public ResponseEntity<CreateEventResponse> create(@RequestBody CreateEventRequest req){
        Long id= eventService.create(req.title,req.startsAt,req.endsAt);
        return ResponseEntity.created(URI.create("/api/events/"+id)).body(new CreateEventResponse(id));
    }
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> get(@PathVariable Long eventId)
    {
        var event=eventService.get(eventId);
        return ResponseEntity.ok(new EventResponse(event.getId(), event.getTitle(), event.getStartsAt(),event.getEndsAt()));
    }
    @PutMapping("/{eventId}")
    public ResponseEntity<Void> update(@PathVariable Long eventId, @RequestBody CreateEventRequest req) {
        eventService.update(eventId, req.title, req.startsAt, req.endsAt);
        return ResponseEntity.noContent().build(); // 204
    }
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(@PathVariable Long eventId){
        eventService.delete(eventId);
        return ResponseEntity.noContent().build();
    }
    @Getter@NoArgsConstructor
    static class CreateEventRequest {
        @NotBlank
        public String title;
        @NotNull
        public LocalDateTime startsAt;
        @NotNull public LocalDateTime endsAt;
    }

    @Getter @AllArgsConstructor
    static class CreateEventResponse {
        private Long id;
    }

    @Getter @AllArgsConstructor
    static class EventResponse {
        private Long id;
        private String title;
        private LocalDateTime startsAt;
        private LocalDateTime endsAt;
    }
}
