package com.eventbox.backend.controller;

import com.eventbox.backend.model.EventType;
import com.eventbox.backend.service.EventTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/event-types")
@CrossOrigin(origins = "*")
public class EventTypeController {

    private final EventTypeService service;

    public EventTypeController(EventTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventType> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<EventType> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public EventType save(@RequestBody EventType eventType) {
        return service.save(eventType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
