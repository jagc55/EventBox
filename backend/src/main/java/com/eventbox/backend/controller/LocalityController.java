package com.eventbox.backend.controller;

import com.eventbox.backend.model.Locality;
import com.eventbox.backend.service.LocalityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/localities")
@CrossOrigin(origins = "*")
public class LocalityController {

    private final LocalityService service;

    public LocalityController(LocalityService service) {
        this.service = service;
    }

    @GetMapping
    public List<Locality> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Locality> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Locality save(@RequestBody Locality locality) {
        return service.save(locality);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
