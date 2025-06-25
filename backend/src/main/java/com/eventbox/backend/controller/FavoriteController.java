package com.eventbox.backend.controller;

import com.eventbox.backend.model.Favorite;
import com.eventbox.backend.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favorite>> getFavoritesByUser(@PathVariable Long userId) {
        try {
            List<Favorite> favorites = service.getFavoritesByUser(userId);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}/event/{eventId}")
    public ResponseEntity<Boolean> isFavorite(@PathVariable Long userId, @PathVariable Long eventId) {
        try {
            boolean isFavorite = service.isFavorite(userId, eventId);
            return ResponseEntity.ok(isFavorite);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/user/{userId}/event/{eventId}")
    public ResponseEntity<Map<String, String>> addToFavorites(@PathVariable Long userId, @PathVariable Long eventId) {
        try {
            service.addToFavorites(userId, eventId);
            return ResponseEntity.ok(Map.of("message", "Evento añadido a favoritos correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @DeleteMapping("/user/{userId}/event/{eventId}")
    public ResponseEntity<Map<String, String>> removeFromFavorites(@PathVariable Long userId, @PathVariable Long eventId) {
        try {
            service.removeFromFavorites(userId, eventId);
            return ResponseEntity.ok(Map.of("message", "Evento eliminado de favoritos correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
}
