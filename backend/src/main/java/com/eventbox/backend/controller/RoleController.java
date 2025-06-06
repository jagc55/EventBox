package com.eventbox.backend.controller;

import com.eventbox.backend.model.Role;
import com.eventbox.backend.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @GetMapping
    public List<Role> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Role> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Role save(@RequestBody Role role) {
        return service.save(role);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
