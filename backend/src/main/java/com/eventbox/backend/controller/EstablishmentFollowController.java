package com.eventbox.backend.controller;

import com.eventbox.backend.model.EstablishmentFollow;
import com.eventbox.backend.service.EstablishmentFollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/establishment-follows")
@CrossOrigin(origins = "*")
public class EstablishmentFollowController {
    
    private final EstablishmentFollowService establishmentFollowService;
    
    public EstablishmentFollowController(EstablishmentFollowService establishmentFollowService) {
        this.establishmentFollowService = establishmentFollowService;
    }
    
    // Seguir a un establecimiento
    @PostMapping
    public ResponseEntity<?> followEstablishment(@RequestBody Map<String, Long> payload) {
        try {
            Long userId = payload.get("userId");
            Long establishmentId = payload.get("establishmentId");
            
            EstablishmentFollow follow = establishmentFollowService.followEstablishment(userId, establishmentId);
            return ResponseEntity.ok(Map.of("message", "Ahora sigues a este establecimiento", "follow", follow));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Dejar de seguir a un establecimiento
    @DeleteMapping
    public ResponseEntity<?> unfollowEstablishment(@RequestBody Map<String, Long> payload) {
        try {
            Long userId = payload.get("userId");
            Long establishmentId = payload.get("establishmentId");
            
            establishmentFollowService.unfollowEstablishment(userId, establishmentId);
            return ResponseEntity.ok(Map.of("message", "Has dejado de seguir a este establecimiento"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Verificar si un usuario sigue a un establecimiento
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> isFollowing(@RequestParam Long userId, @RequestParam Long establishmentId) {
        boolean isFollowing = establishmentFollowService.isFollowing(userId, establishmentId);
        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    }
    
    // Obtener establecimientos que sigue un usuario
    @GetMapping("/user/{userId}/establishments")
    public ResponseEntity<?> getFollowedEstablishments(@PathVariable Long userId) {
        try {
            List<EstablishmentFollow> establishments = establishmentFollowService.getFollowedEstablishments(userId);
            
            // Convertir a un formato más simple para evitar problemas de serialización
            List<Map<String, Object>> result = establishments.stream()
                .map(follow -> {
                    Map<String, Object> establishmentInfo = new HashMap<>();
                    establishmentInfo.put("id", follow.getEstablishment().getId());
                    establishmentInfo.put("name", follow.getEstablishment().getName());
                    establishmentInfo.put("address", follow.getEstablishment().getAddress());
                    establishmentInfo.put("email", follow.getEstablishment().getEmail());
                    establishmentInfo.put("phone", follow.getEstablishment().getPhone());
                    establishmentInfo.put("createdAt", follow.getCreatedAt());
                    return establishmentInfo;
                })
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Obtener seguidores de un establecimiento
    @GetMapping("/establishment/{establishmentId}/followers")
    public ResponseEntity<List<EstablishmentFollow>> getEstablishmentFollowers(@PathVariable Long establishmentId) {
        try {
            List<EstablishmentFollow> followers = establishmentFollowService.getEstablishmentFollowers(establishmentId);
            return ResponseEntity.ok(followers);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Obtener conteos de establecimientos seguidos por un usuario
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> getFollowedEstablishmentsCount(@PathVariable Long userId) {
        long count = establishmentFollowService.getFollowedEstablishmentsCount(userId);
        return ResponseEntity.ok(Map.of("establishmentsFollowingCount", count));
    }
    
    // Obtener conteos de seguidores de un establecimiento
    @GetMapping("/establishment/{establishmentId}/count")
    public ResponseEntity<Map<String, Long>> getEstablishmentFollowersCount(@PathVariable Long establishmentId) {
        long count = establishmentFollowService.getEstablishmentFollowersCount(establishmentId);
        return ResponseEntity.ok(Map.of("followersCount", count));
    }
    
    // Obtener establecimientos más seguidos
    @GetMapping("/most-followed")
    public ResponseEntity<List<Object[]>> getMostFollowedEstablishments() {
        List<Object[]> mostFollowed = establishmentFollowService.getMostFollowedEstablishments();
        return ResponseEntity.ok(mostFollowed);
    }
}
