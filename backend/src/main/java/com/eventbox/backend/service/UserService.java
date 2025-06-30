package com.eventbox.backend.service;

import com.eventbox.backend.model.Role;
import com.eventbox.backend.model.User;
import com.eventbox.backend.repository.UserRepository;
import com.eventbox.backend.repository.RoleRepository;
import com.eventbox.backend.repository.EstablishmentRepository;
import com.eventbox.backend.repository.EventRepository;
import com.eventbox.backend.repository.FavoriteRepository;
import com.eventbox.backend.repository.UserFollowRepository;
import com.eventbox.backend.repository.EstablishmentFollowRepository;
import com.eventbox.backend.repository.CommentRepository;
import com.eventbox.backend.repository.OrganizerRequestRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @Autowired
    private EstablishmentRepository establishmentRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private UserFollowRepository userFollowRepository;
    
    @Autowired
    private EstablishmentFollowRepository establishmentFollowRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private OrganizerRequestRepository organizerRequestRepository;

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

    @Transactional
    public void deleteUser(Long id) {
        // Eliminar favoritos del usuario
        favoriteRepository.deleteByUserId(id);
        // Eliminar favoritos de eventos organizados por el usuario
        favoriteRepository.deleteByEventOrganizerId(id);
        // Eliminar comentarios de eventos organizados por el usuario
        commentRepository.deleteByEventOrganizerId(id);
        // Eliminar seguidores y seguidos
        userFollowRepository.deleteByUserId(id);
        // Eliminar establecimientos seguidos
        establishmentFollowRepository.deleteByUserId(id);
        // Eliminar follows de establecimientos cuyo propietario es el usuario
        establishmentFollowRepository.deleteByEstablishmentOwnerId(id);
        // Eliminar comentarios
        commentRepository.deleteByUserId(id);
        // Eliminar solicitudes de organizador
        organizerRequestRepository.deleteByUserId(id);
        // Eliminar eventos si es organizador
        eventRepository.deleteByOrganizerId(id);
        // Eliminar establecimientos si es organizador
        establishmentRepository.deleteByUserId(id);
        // Finalmente eliminar el usuario
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

    public ResponseEntity<Map<String, String>> demoteOrganizer(Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
            }

            User user = userOpt.get();
            // Verificar que el usuario es un organizador
            if (user.getRole().getId() != 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El usuario no es un organizador"));
            }

            // Eliminar favoritos de eventos organizados por el usuario
            favoriteRepository.deleteByEventOrganizerId(userId);
            // Eliminar follows de establecimientos cuyo propietario es el usuario
            establishmentFollowRepository.deleteByEstablishmentOwnerId(userId);
            // Eliminar comentarios de eventos organizados por el usuario
            commentRepository.deleteByEventOrganizerId(userId);
            // Eliminar todos los eventos del usuario
            eventRepository.deleteByOrganizerId(userId);
            // Eliminar todos los establecimientos del usuario
            establishmentRepository.deleteByUserId(userId);
            // Eliminar todos sus seguidores (pero NO los seguidos)
            userFollowRepository.deleteByFollowedId(userId);

            // Cambiar el rol a usuario normal (ID = 1)
            Optional<Role> userRole = roleRepository.findById(1L);
            if (userRole.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del sistema"));
            }

            user.setRole(userRole.get());
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "Rol de organizador removido correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al quitar rol de organizador: " + e.getMessage()));
        }
    }
}