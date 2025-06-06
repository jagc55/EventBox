package com.eventbox.backend.controller;

import com.eventbox.backend.model.Favorite;
import com.eventbox.backend.model.FavoriteId;
import com.eventbox.backend.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    private final FavoriteService service;

    public FavoriteController(FavoriteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Favorite> getAll() {
        return service.getAll();
    }

    @GetMapping("/user/{userId}/event/{eventId}")
    public Optional<Favorite> getById(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getById(new FavoriteId(userId, eventId));
    }

    @PostMapping
    public Favorite save(@RequestBody Favorite favorite) {
        return service.save(favorite);
    }

    @DeleteMapping("/user/{userId}/event/{eventId}")
    public void delete(@PathVariable Long userId, @PathVariable Long eventId) {
        service.delete(new FavoriteId(userId, eventId));
    }
}
