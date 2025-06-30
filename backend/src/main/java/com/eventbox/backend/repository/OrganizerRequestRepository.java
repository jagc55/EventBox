package com.eventbox.backend.repository;

import com.eventbox.backend.model.OrganizerRequest;
import com.eventbox.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizerRequestRepository extends JpaRepository<OrganizerRequest, Long> {
    
    // Buscar solicitud pendiente de un usuario específico
    Optional<OrganizerRequest> findByUserAndStatus(User user, OrganizerRequest.RequestStatus status);
    
    // Buscar todas las solicitudes de un usuario
    List<OrganizerRequest> findByUserOrderByRequestedAtDesc(User user);
    
    // Buscar todas las solicitudes por estado
    List<OrganizerRequest> findByStatusOrderByRequestedAtAsc(OrganizerRequest.RequestStatus status);
    
    // Verificar si un usuario ya tiene una solicitud pendiente
    boolean existsByUserAndStatus(User user, OrganizerRequest.RequestStatus status);
    
    // Contar solicitudes pendientes
    long countByStatus(OrganizerRequest.RequestStatus status);
    
    // Eliminar todas las solicitudes de organizador de un usuario
    @Modifying
    @Transactional
    @Query("DELETE FROM OrganizerRequest o WHERE o.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
