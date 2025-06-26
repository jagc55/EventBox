package com.eventbox.backend.controller;

import com.eventbox.backend.model.OrganizerRequest;
import com.eventbox.backend.service.OrganizerRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organizer-requests")
@CrossOrigin(origins = "*")
public class OrganizerRequestController {
    
    private final OrganizerRequestService organizerRequestService;
    
    public OrganizerRequestController(OrganizerRequestService organizerRequestService) {
        this.organizerRequestService = organizerRequestService;
    }
    
    // Crear una nueva solicitud de organizador
    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody Map<String, Long> payload) {
        try {
            Long userId = payload.get("userId");
            OrganizerRequest request = organizerRequestService.createRequest(userId);
            return ResponseEntity.ok(Map.of("message", "Solicitud enviada correctamente", "request", request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Obtener todas las solicitudes pendientes (solo para administradores)
    @GetMapping("/pending")
    public ResponseEntity<List<OrganizerRequest>> getPendingRequests() {
        try {
            List<OrganizerRequest> requests = organizerRequestService.getPendingRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Obtener solicitudes de un usuario específico
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrganizerRequest>> getUserRequests(@PathVariable Long userId) {
        try {
            List<OrganizerRequest> requests = organizerRequestService.getUserRequests(userId);
            return ResponseEntity.ok(requests);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Aprobar una solicitud
    @PatchMapping("/{requestId}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long requestId, @RequestBody Map<String, Object> payload) {
        try {
            Long adminId = ((Number) payload.get("adminId")).longValue();
            
            OrganizerRequest request = organizerRequestService.approveRequest(requestId, adminId);
            return ResponseEntity.ok(Map.of("message", "Solicitud aprobada correctamente", "request", request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Rechazar una solicitud
    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long requestId, @RequestBody Map<String, Object> payload) {
        try {
            Long adminId = ((Number) payload.get("adminId")).longValue();
            
            OrganizerRequest request = organizerRequestService.rejectRequest(requestId, adminId);
            return ResponseEntity.ok(Map.of("message", "Solicitud rechazada", "request", request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Obtener conteo de solicitudes pendientes
    @GetMapping("/pending/count")
    public ResponseEntity<Map<String, Long>> getPendingRequestsCount() {
        long count = organizerRequestService.getPendingRequestsCount();
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    // Verificar si un usuario tiene solicitud pendiente
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<Map<String, Boolean>> hasPendingRequest(@PathVariable Long userId) {
        boolean hasPending = organizerRequestService.hasPendingRequest(userId);
        return ResponseEntity.ok(Map.of("hasPendingRequest", hasPending));
    }
}
