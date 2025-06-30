package com.eventbox.backend.service;

import com.eventbox.backend.model.User;
import com.eventbox.backend.model.UserFollow;
import com.eventbox.backend.repository.UserFollowRepository;
import com.eventbox.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserFollowService {
    
    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;
    
    public UserFollowService(UserFollowRepository userFollowRepository, UserRepository userRepository) {
        this.userFollowRepository = userFollowRepository;
        this.userRepository = userRepository;
    }
    
    // Seguir a un usuario
    public UserFollow followUser(Long followerId, Long followedId) {
        if (followerId.equals(followedId)) {
            throw new RuntimeException("No puedes seguirte a ti mismo");
        }
        
        Optional<User> followerOptional = userRepository.findById(followerId);
        Optional<User> followedOptional = userRepository.findById(followedId);
        
        if (followerOptional.isEmpty() || followedOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        User follower = followerOptional.get();
        User followed = followedOptional.get();
        
        // Verificar que solo se pueda seguir a organizadores y administradores
        if (followed.getRole().getId() < 2) {
            throw new RuntimeException("Solo puedes seguir a organizadores y administradores");
        }
        
        // Verificar si ya lo sigue
        if (userFollowRepository.existsByFollowerAndFollowed(follower, followed)) {
            throw new RuntimeException("Ya sigues a este usuario");
        }
        
        // Crear la relación de seguimiento
        UserFollow userFollow = new UserFollow(follower, followed);
        userFollow = userFollowRepository.save(userFollow);
        
        // Actualizar contadores
        follower.setFollowingCount(follower.getFollowingCount() + 1);
        followed.setFollowersCount(followed.getFollowersCount() + 1);
        
        userRepository.save(follower);
        userRepository.save(followed);
        
        return userFollow;
    }
    
    // Dejar de seguir a un usuario
    public void unfollowUser(Long followerId, Long followedId) {
        Optional<User> followerOptional = userRepository.findById(followerId);
        Optional<User> followedOptional = userRepository.findById(followedId);
        
        if (followerOptional.isEmpty() || followedOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        User follower = followerOptional.get();
        User followed = followedOptional.get();
        
        // Verificar si existe la relación
        Optional<UserFollow> userFollowOptional = userFollowRepository.findByFollowerAndFollowed(follower, followed);
        if (userFollowOptional.isEmpty()) {
            throw new RuntimeException("No sigues a este usuario");
        }
        
        // Eliminar la relación
        userFollowRepository.delete(userFollowOptional.get());
        
        // Actualizar contadores
        follower.setFollowingCount(Math.max(0, follower.getFollowingCount() - 1));
        followed.setFollowersCount(Math.max(0, followed.getFollowersCount() - 1));
        
        userRepository.save(follower);
        userRepository.save(followed);
    }
    
    // Verificar si un usuario sigue a otro
    public boolean isFollowing(Long followerId, Long followedId) {
        Optional<User> followerOptional = userRepository.findById(followerId);
        Optional<User> followedOptional = userRepository.findById(followedId);
        
        if (followerOptional.isEmpty() || followedOptional.isEmpty()) {
            return false;
        }
        
        return userFollowRepository.existsByFollowerAndFollowed(followerOptional.get(), followedOptional.get());
    }
    
    // Obtener seguidores de un usuario
    public List<UserFollow> getFollowers(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        return userFollowRepository.findByFollowedWithFollowerOrderByCreatedAtDesc(userOptional.get());
    }
    
    // Obtener usuarios que sigue un usuario
    public List<UserFollow> getFollowing(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        return userFollowRepository.findByFollowerWithFollowedOrderByCreatedAtDesc(userOptional.get());
    }
    
    // Obtener conteo de seguidores
    public long getFollowersCount(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return 0;
        }
        
        return userFollowRepository.countByFollowed(userOptional.get());
    }
    
    // Obtener conteo de usuarios que sigue
    public long getFollowingCount(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return 0;
        }
        
        return userFollowRepository.countByFollower(userOptional.get());
    }
    
    // Obtener usuarios más seguidos
    public List<Object[]> getMostFollowedUsers() {
        return userFollowRepository.findMostFollowedUsers();
    }
}
