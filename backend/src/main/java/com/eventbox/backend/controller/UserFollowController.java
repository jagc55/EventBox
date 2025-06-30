package com.eventbox.backend.controller;

import com.eventbox.backend.model.UserFollow;
import com.eventbox.backend.service.UserFollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-follows")
@CrossOrigin(origins = "*")
public class UserFollowController {
    
    private final UserFollowService userFollowService;
    
    public UserFollowController(UserFollowService userFollowService) {
        this.userFollowService = userFollowService;
    }
    
    // Seguir a un usuario
    @PostMapping
    public ResponseEntity<?> followUser(@RequestBody Map<String, Long> payload) {
        try {
            Long followerId = payload.get("followerId");
            Long followedId = payload.get("followedId");
            
            UserFollow userFollow = userFollowService.followUser(followerId, followedId);
            return ResponseEntity.ok(Map.of("message", "Ahora sigues a este usuario", "follow", userFollow));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Dejar de seguir a un usuario
    @DeleteMapping
    public ResponseEntity<?> unfollowUser(@RequestBody Map<String, Long> payload) {
        try {
            Long followerId = payload.get("followerId");
            Long followedId = payload.get("followedId");
            
            userFollowService.unfollowUser(followerId, followedId);
            return ResponseEntity.ok(Map.of("message", "Has dejado de seguir a este usuario"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Verificar si un usuario sigue a otro
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> isFollowing(@RequestParam Long followerId, @RequestParam Long followedId) {
        boolean isFollowing = userFollowService.isFollowing(followerId, followedId);
        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    }
    
    // Obtener seguidores de un usuario
    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable Long userId) {
        try {
            List<UserFollow> followers = userFollowService.getFollowers(userId);
            
            // Convertir a un formato más simple para evitar problemas de serialización
            List<Map<String, Object>> result = followers.stream()
                .map(follow -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", follow.getFollower().getId());
                    userInfo.put("name", follow.getFollower().getName());
                    userInfo.put("email", follow.getFollower().getEmail());
                    userInfo.put("createdAt", follow.getCreatedAt());
                    return userInfo;
                })
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Obtener usuarios que sigue un usuario
    @GetMapping("/{userId}/following")
    public ResponseEntity<?> getFollowing(@PathVariable Long userId) {
        try {
            List<UserFollow> following = userFollowService.getFollowing(userId);
            
            // Convertir a un formato más simple para evitar problemas de serialización
            List<Map<String, Object>> result = following.stream()
                .map(follow -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", follow.getFollowed().getId());
                    userInfo.put("name", follow.getFollowed().getName());
                    userInfo.put("email", follow.getFollowed().getEmail());
                    userInfo.put("createdAt", follow.getCreatedAt());
                    return userInfo;
                })
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Obtener conteos de seguidores y siguiendo
    @GetMapping("/{userId}/counts")
    public ResponseEntity<Map<String, Long>> getFollowCounts(@PathVariable Long userId) {
        long followersCount = userFollowService.getFollowersCount(userId);
        long followingCount = userFollowService.getFollowingCount(userId);
        
        return ResponseEntity.ok(Map.of(
            "followersCount", followersCount,
            "followingCount", followingCount
        ));
    }
    
    // Obtener usuarios más seguidos
    @GetMapping("/most-followed")
    public ResponseEntity<List<Object[]>> getMostFollowedUsers() {
        List<Object[]> mostFollowed = userFollowService.getMostFollowedUsers();
        return ResponseEntity.ok(mostFollowed);
    }
}
