package com.eventbox.backend.controller;

import com.eventbox.backend.dto.LoginRequest;
import com.eventbox.backend.dto.RegisterRequest;
import com.eventbox.backend.model.Role;
import com.eventbox.backend.model.User;
import com.eventbox.backend.repository.RoleRepository;
import com.eventbox.backend.repository.UserRepository;
import com.eventbox.backend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        // Verificar si el email ya existe
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El correo electrónico ya está registrado"));
        }
        
        // Verificar si el nombre de usuario ya existe
        if (userRepository.findByName(request.getName()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El nombre de usuario ya está en uso"));
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("roleId", user.getRole().getId());
        response.put("email", user.getEmail());
        response.put("name",  user.getName());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtService.generateToken(request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("roleId", user.getRole().getId());
        response.put("email", user.getEmail());
        response.put("name",  user.getName());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshUserInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remover "Bearer "
            String email = jwtService.extractUsername(token);
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("roleId", user.getRole().getId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token inválido"));
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = userRepository.findByName(username).isPresent();
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
