package com.eventbox.backend.controller;

import com.eventbox.backend.model.Province;
import com.eventbox.backend.service.ProvinceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/provinces")
@CrossOrigin(origins = "*")
public class ProvinceController {

    private final ProvinceService service;

    public ProvinceController(ProvinceService service) {
        this.service = service;
    }

    @GetMapping
    public List<Province> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Province> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Province save(@RequestBody Province province) {
        return service.save(province);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
