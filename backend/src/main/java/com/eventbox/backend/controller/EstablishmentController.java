package com.eventbox.backend.controller;

import com.eventbox.backend.model.Establishment;
import com.eventbox.backend.model.Event;
import com.eventbox.backend.service.EstablishmentService;
import com.eventbox.backend.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/establishments")
@CrossOrigin(origins = "*")
public class EstablishmentController {

    private final EstablishmentService service;
    private final EventService eventService;

    public EstablishmentController(EstablishmentService service, EventService eventService) {
        this.service = service;
        this.eventService = eventService;
    }

    @GetMapping
    public List<Establishment> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Establishment> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Establishment save(@RequestBody Establishment establishment) {
        return service.save(establishment);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/user/{userId}")
    public List<Establishment> getByUserId(@PathVariable Long userId) {
        return service.getByUserId(userId);
    }

    @GetMapping("/{id}/events")
    public List<Event> getEventsByEstablishment(@PathVariable Long id) {
        return eventService.findByEstablishmentId(id);
    }
}
