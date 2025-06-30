package com.eventbox.backend.service;

import com.eventbox.backend.model.Establishment;
import com.eventbox.backend.model.EstablishmentFollow;
import com.eventbox.backend.model.User;
import com.eventbox.backend.repository.EstablishmentFollowRepository;
import com.eventbox.backend.repository.EstablishmentRepository;
import com.eventbox.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EstablishmentFollowService {
    
    private final EstablishmentFollowRepository establishmentFollowRepository;
    private final UserRepository userRepository;
    private final EstablishmentRepository establishmentRepository;
    
    public EstablishmentFollowService(EstablishmentFollowRepository establishmentFollowRepository, 
                                    UserRepository userRepository,
                                    EstablishmentRepository establishmentRepository) {
        this.establishmentFollowRepository = establishmentFollowRepository;
        this.userRepository = userRepository;
        this.establishmentRepository = establishmentRepository;
    }
    
    // Seguir a un establecimiento
    public EstablishmentFollow followEstablishment(Long userId, Long establishmentId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Establishment> establishmentOptional = establishmentRepository.findById(establishmentId);
        
        if (userOptional.isEmpty() || establishmentOptional.isEmpty()) {
            throw new RuntimeException("Usuario o establecimiento no encontrado");
        }
        
        User user = userOptional.get();
        Establishment establishment = establishmentOptional.get();
        
        // Verificar si ya lo sigue
        if (establishmentFollowRepository.existsByFollowerAndEstablishment(user, establishment)) {
            throw new RuntimeException("Ya sigues a este establecimiento");
        }
        
        // Crear la relación de seguimiento
        EstablishmentFollow establishmentFollow = new EstablishmentFollow(user, establishment);
        return establishmentFollowRepository.save(establishmentFollow);
    }
    
    // Dejar de seguir a un establecimiento
    public void unfollowEstablishment(Long userId, Long establishmentId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Establishment> establishmentOptional = establishmentRepository.findById(establishmentId);
        
        if (userOptional.isEmpty() || establishmentOptional.isEmpty()) {
            throw new RuntimeException("Usuario o establecimiento no encontrado");
        }
        
        User user = userOptional.get();
        Establishment establishment = establishmentOptional.get();
        
        // Verificar si existe la relación
        Optional<EstablishmentFollow> followOptional = 
            establishmentFollowRepository.findByFollowerAndEstablishment(user, establishment);
        if (followOptional.isEmpty()) {
            throw new RuntimeException("No sigues a este establecimiento");
        }
        
        // Eliminar la relación
        establishmentFollowRepository.delete(followOptional.get());
    }
    
    // Verificar si un usuario sigue a un establecimiento
    public boolean isFollowing(Long userId, Long establishmentId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Establishment> establishmentOptional = establishmentRepository.findById(establishmentId);
        
        if (userOptional.isEmpty() || establishmentOptional.isEmpty()) {
            return false;
        }
        
        return establishmentFollowRepository.existsByFollowerAndEstablishment(
            userOptional.get(), establishmentOptional.get());
    }
    
    // Obtener establecimientos que sigue un usuario
    public List<EstablishmentFollow> getFollowedEstablishments(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        return establishmentFollowRepository.findByFollowerWithEstablishmentOrderByCreatedAtDesc(userOptional.get());
    }
    
    // Obtener seguidores de un establecimiento
    public List<EstablishmentFollow> getEstablishmentFollowers(Long establishmentId) {
        Optional<Establishment> establishmentOptional = establishmentRepository.findById(establishmentId);
        if (establishmentOptional.isEmpty()) {
            throw new RuntimeException("Establecimiento no encontrado");
        }
        
        return establishmentFollowRepository.findByEstablishmentWithFollowerOrderByCreatedAtDesc(establishmentOptional.get());
    }

    // Obtener conteo de establecimientos que sigue un usuario
    public long getFollowedEstablishmentsCount(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return 0;
        }
        
        return establishmentFollowRepository.countByFollower(userOptional.get());
    }
    
    // Obtener conteo de seguidores de un establecimiento
    public long getEstablishmentFollowersCount(Long establishmentId) {
        Optional<Establishment> establishmentOptional = establishmentRepository.findById(establishmentId);
        if (establishmentOptional.isEmpty()) {
            return 0;
        }
        
        return establishmentFollowRepository.countByEstablishment(establishmentOptional.get());
    }
    
    // Obtener establecimientos más seguidos
    public List<Object[]> getMostFollowedEstablishments() {
        return establishmentFollowRepository.findMostFollowedEstablishments();
    }
    
    // Eliminar todos los follows de un establecimiento
    public void deleteByEstablishmentId(Long establishmentId) {
        establishmentFollowRepository.deleteByEstablishmentId(establishmentId);
    }
}
