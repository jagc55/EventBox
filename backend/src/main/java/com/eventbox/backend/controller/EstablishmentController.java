package com.eventbox.backend.controller;

import com.eventbox.backend.model.Establishment;
import com.eventbox.backend.model.Event;
import com.eventbox.backend.model.Locality;
import com.eventbox.backend.service.EstablishmentService;
import com.eventbox.backend.service.EventService;
import com.eventbox.backend.repository.LocalityRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/establishments")
@CrossOrigin(origins = "*")
public class EstablishmentController {

    private final EstablishmentService service;
    private final EventService eventService;
    private final LocalityRepository localityRepository;

    public EstablishmentController(EstablishmentService service, EventService eventService, LocalityRepository localityRepository) {
        this.service = service;
        this.eventService = eventService;
        this.localityRepository = localityRepository;
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
    public ResponseEntity<?> delete(@PathVariable Long id) {
        // Eliminar todos los eventos asociados antes de eliminar el establecimiento
        List<Event> events = eventService.findByEstablishmentId(id);
        for (Event event : events) {
            eventService.delete(event.getId());
        }
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public List<Establishment> getByUserId(@PathVariable Long userId) {
        return service.getByUserId(userId);
    }

    @GetMapping("/{id}/events")
    public List<Event> getEventsByEstablishment(@PathVariable Long id) {
        return eventService.findByEstablishmentId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEstablishment(@PathVariable Long id, @RequestBody Establishment updated) {
        return service.getById(id).map(est -> {
            est.setName(updated.getName());
            est.setAddress(updated.getAddress());
            est.setEmail(updated.getEmail());
            est.setPhone(updated.getPhone());
            // Buscar la localidad por nombre y provincia
            Locality loc = localityRepository.findAll().stream()
                .filter(l -> l.getName().equals(updated.getLocality().getName()) &&
                             l.getProvince().getName().equals(updated.getLocality().getProvince().getName()))
                .findFirst().orElse(null);
            if (loc != null) {
                est.setLocality(loc);
            }
            Establishment saved = service.save(est);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }
}
