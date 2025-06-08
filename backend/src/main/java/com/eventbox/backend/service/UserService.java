package com.eventbox.backend.service;

import com.eventbox.backend.model.Role;
import com.eventbox.backend.model.User;
import com.eventbox.backend.repository.UserRepository;
import com.eventbox.backend.repository.RoleRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public ResponseEntity<String> updateUserRole(Long userId, Integer roleId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        User user = userOpt.get();
        Optional<Role> roleOpt = roleRepository.findById(Long.valueOf(roleId));
        if (roleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rol inválido");
        }

        user.setRole(roleOpt.get());
        userRepository.save(user);

    return ResponseEntity.ok("Rol actualizado correctamente");
}
}