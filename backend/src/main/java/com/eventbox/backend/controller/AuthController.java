package com.eventbox.backend.controller;

import com.eventbox.backend.dto.JwtResponse;
import com.eventbox.backend.dto.LoginRequest;
import com.eventbox.backend.dto.RegisterRequest;
import com.eventbox.backend.model.Role;
import com.eventbox.backend.model.User;
import com.eventbox.backend.repository.RoleRepository;
import com.eventbox.backend.repository.UserRepository;
import com.eventbox.backend.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public JwtResponse register(@RequestBody RegisterRequest request) {
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new JwtResponse(token);
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest request) {
        var auth = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );

        authenticationManager.authenticate(auth);

        String token = jwtService.generateToken(request.getEmail());
        return new JwtResponse(token);
    }
}
