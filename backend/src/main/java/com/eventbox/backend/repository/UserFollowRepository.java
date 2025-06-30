package com.eventbox.backend.repository;

import com.eventbox.backend.model.User;
import com.eventbox.backend.model.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    
    // Verificar si un usuario sigue a otro
    boolean existsByFollowerAndFollowed(User follower, User followed);
    
    // Buscar la relación de seguimiento específica
    Optional<UserFollow> findByFollowerAndFollowed(User follower, User followed);
    
    // Obtener todos los seguidores de un usuario
    List<UserFollow> findByFollowedOrderByCreatedAtDesc(User followed);
    
    // Obtener todos los usuarios que sigue un usuario
    List<UserFollow> findByFollowerOrderByCreatedAtDesc(User follower);
    
    // Obtener seguidores con JOIN FETCH para evitar lazy loading
    @Query("SELECT uf FROM UserFollow uf JOIN FETCH uf.follower WHERE uf.followed = :user ORDER BY uf.createdAt DESC")
    List<UserFollow> findByFollowedWithFollowerOrderByCreatedAtDesc(User user);
    
    // Obtener usuarios seguidos con JOIN FETCH para evitar lazy loading
    @Query("SELECT uf FROM UserFollow uf JOIN FETCH uf.followed WHERE uf.follower = :user ORDER BY uf.createdAt DESC")
    List<UserFollow> findByFollowerWithFollowedOrderByCreatedAtDesc(User user);
    
    // Contar seguidores de un usuario
    long countByFollowed(User followed);
    
    // Contar usuarios que sigue un usuario
    long countByFollower(User follower);
    
    // Obtener usuarios más seguidos
    @Query("SELECT uf.followed, COUNT(uf) as followersCount " +
           "FROM UserFollow uf " +
           "GROUP BY uf.followed " +
           "ORDER BY followersCount DESC")
    List<Object[]> findMostFollowedUsers();
    
    // Eliminar relación de seguimiento
    void deleteByFollowerAndFollowed(User follower, User followed);

    // Eliminar todos los seguimientos donde el usuario es seguidor o seguido
    @Modifying
    @Transactional
    @Query("DELETE FROM UserFollow uf WHERE uf.follower.id = :userId OR uf.followed.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // Eliminar todos los seguidores de un usuario (donde él es seguido)
    @Modifying
    @Transactional
    @Query("DELETE FROM UserFollow uf WHERE uf.followed.id = :userId")
    void deleteByFollowedId(@Param("userId") Long userId);
}
