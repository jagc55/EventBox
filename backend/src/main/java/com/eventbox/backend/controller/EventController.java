package com.eventbox.backend.controller;

import com.eventbox.backend.model.Event;
import com.eventbox.backend.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<Event> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Event> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Event save(@RequestBody Event event) {
        return service.save(event);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/establishments/{id}/events")
    public List<Event> getEventsByEstablishment(@PathVariable Long id) {
        return service.findByEstablishmentId(id);
    }
}
