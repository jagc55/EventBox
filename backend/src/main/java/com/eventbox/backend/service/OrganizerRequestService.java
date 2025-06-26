package com.eventbox.backend.service;

import com.eventbox.backend.model.OrganizerRequest;
import com.eventbox.backend.model.User;
import com.eventbox.backend.repository.OrganizerRequestRepository;
import com.eventbox.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrganizerRequestService {
    
    private final OrganizerRequestRepository organizerRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    
    public OrganizerRequestService(OrganizerRequestRepository organizerRequestRepository, 
                                   UserRepository userRepository, 
                                   UserService userService) {
        this.organizerRequestRepository = organizerRequestRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }
    
    // Crear una nueva solicitud de organizador
    public OrganizerRequest createRequest(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        User user = userOptional.get();
        
        // Verificar si ya es organizador o administrador
        if (user.getRole().getId() >= 2) {
            throw new RuntimeException("El usuario ya tiene rol de organizador o administrador");
        }
        
        // Verificar si ya tiene una solicitud pendiente
        if (organizerRequestRepository.existsByUserAndStatus(user, OrganizerRequest.RequestStatus.PENDING)) {
            throw new RuntimeException("Ya tienes una solicitud pendiente");
        }
        
        OrganizerRequest request = new OrganizerRequest(user);
        return organizerRequestRepository.save(request);
    }
    
    // Obtener todas las solicitudes pendientes
    public List<OrganizerRequest> getPendingRequests() {
        return organizerRequestRepository.findByStatusOrderByRequestedAtAsc(OrganizerRequest.RequestStatus.PENDING);
    }
    
    // Obtener solicitudes de un usuario específico
    public List<OrganizerRequest> getUserRequests(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        return organizerRequestRepository.findByUserOrderByRequestedAtDesc(userOptional.get());
    }
    
    // Aprobar una solicitud
    public OrganizerRequest approveRequest(Long requestId, Long adminId, String comments) {
        Optional<OrganizerRequest> requestOptional = organizerRequestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            throw new RuntimeException("Solicitud no encontrada");
        }
        
        Optional<User> adminOptional = userRepository.findById(adminId);
        if (adminOptional.isEmpty()) {
            throw new RuntimeException("Administrador no encontrado");
        }
        
        OrganizerRequest request = requestOptional.get();
        User admin = adminOptional.get();
        
        // Verificar que el admin tiene permisos
        if (admin.getRole().getId() != 3) {
            throw new RuntimeException("No tienes permisos para aprobar solicitudes");
        }
        
        // Actualizar la solicitud
        request.setStatus(OrganizerRequest.RequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(admin);
        request.setComments(comments);
        
        // Cambiar el rol del usuario a organizador
        userService.updateUserRole(request.getUser().getId(), 2);
        
        return organizerRequestRepository.save(request);
    }
    
    // Rechazar una solicitud
    public OrganizerRequest rejectRequest(Long requestId, Long adminId, String comments) {
        Optional<OrganizerRequest> requestOptional = organizerRequestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            throw new RuntimeException("Solicitud no encontrada");
        }
        
        Optional<User> adminOptional = userRepository.findById(adminId);
        if (adminOptional.isEmpty()) {
            throw new RuntimeException("Administrador no encontrado");
        }
        
        OrganizerRequest request = requestOptional.get();
        User admin = adminOptional.get();
        
        // Verificar que el admin tiene permisos
        if (admin.getRole().getId() != 3) {
            throw new RuntimeException("No tienes permisos para rechazar solicitudes");
        }
        
        // Actualizar la solicitud
        request.setStatus(OrganizerRequest.RequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(admin);
        request.setComments(comments);
        
        return organizerRequestRepository.save(request);
    }
    
    // Obtener el conteo de solicitudes pendientes
    public long getPendingRequestsCount() {
        return organizerRequestRepository.countByStatus(OrganizerRequest.RequestStatus.PENDING);
    }
    
    // Verificar si un usuario tiene una solicitud pendiente
    public boolean hasPendingRequest(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }
        
        return organizerRequestRepository.existsByUserAndStatus(userOptional.get(), OrganizerRequest.RequestStatus.PENDING);
    }
}
