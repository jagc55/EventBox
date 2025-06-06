package com.eventbox.backend.controller;

import com.eventbox.backend.model.Establishment;
import com.eventbox.backend.service.EstablishmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/establishments")
@CrossOrigin(origins = "*")
public class EstablishmentController {

    private final EstablishmentService service;

    public EstablishmentController(EstablishmentService service) {
        this.service = service;
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
}
