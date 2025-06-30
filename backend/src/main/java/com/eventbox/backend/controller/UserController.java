package com.eventbox.backend.controller;

import com.eventbox.backend.model.User;
import com.eventbox.backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Para permitir conexión desde Vue
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        return userService.updateUserRole(id, payload.get("roleId"));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Map<String, String>> updateUserPassword(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");
        
        try {
            Optional<User> userOptional = userService.getUserById(id);
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado"));
            }
            
            User user = userOptional.get();
            
            // Verificar que la contraseña actual es correcta
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Contraseña actual incorrecta"));
            }
            
            // Actualizar la contraseña
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.saveUser(user);
            
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al actualizar la contraseña"));
        }
    }

    @PatchMapping("/{id}/demote")
    public ResponseEntity<Map<String, String>> demoteOrganizer(@PathVariable Long id) {
        return userService.demoteOrganizer(id);
    }
}
