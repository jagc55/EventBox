package com.eventbox.backend.repository;

import com.eventbox.backend.model.Establishment;
import com.eventbox.backend.model.EstablishmentFollow;
import com.eventbox.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstablishmentFollowRepository extends JpaRepository<EstablishmentFollow, Long> {
    
    // Verificar si un usuario sigue a un establecimiento
    boolean existsByFollowerAndEstablishment(User follower, Establishment establishment);
    
    // Buscar relación específica
    Optional<EstablishmentFollow> findByFollowerAndEstablishment(User follower, Establishment establishment);
    
    // Obtener establecimientos que sigue un usuario
    List<EstablishmentFollow> findByFollowerOrderByCreatedAtDesc(User follower);
    
    // Obtener seguidores de un establecimiento
    List<EstablishmentFollow> findByEstablishmentOrderByCreatedAtDesc(Establishment establishment);
    
    // Obtener establecimientos seguidos con JOIN FETCH para evitar lazy loading
    @Query("SELECT ef FROM EstablishmentFollow ef JOIN FETCH ef.establishment WHERE ef.follower = :user ORDER BY ef.createdAt DESC")
    List<EstablishmentFollow> findByFollowerWithEstablishmentOrderByCreatedAtDesc(User user);
    
    // Obtener seguidores de establecimiento con JOIN FETCH para evitar lazy loading
    @Query("SELECT ef FROM EstablishmentFollow ef JOIN FETCH ef.follower WHERE ef.establishment = :establishment ORDER BY ef.createdAt DESC")
    List<EstablishmentFollow> findByEstablishmentWithFollowerOrderByCreatedAtDesc(Establishment establishment);
    
    // Contar seguidores de un establecimiento
    long countByEstablishment(Establishment establishment);
    
    // Contar establecimientos que sigue un usuario
    long countByFollower(User follower);
    
    // Obtener establecimientos más seguidos
    @Query("SELECT ef.establishment, COUNT(ef) as followCount " +
           "FROM EstablishmentFollow ef " +
           "GROUP BY ef.establishment " +
           "ORDER BY followCount DESC")
    List<Object[]> findMostFollowedEstablishments();
}
