package com.hookflow.controller;
import com.hookflow.dto.CreateEventRequest;
import com.hookflow.dto.EventResponse;
import com.hookflow.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    public EventController(EventService eventService){
        this.eventService = eventService;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@Valid @RequestBody CreateEventRequest request){
        return eventService.createEvent(request);
    }
}
