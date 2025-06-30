package com.eventbox.backend.repository;

import com.eventbox.backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventIdOrderByDateDesc(Long eventId);

    // Eliminar todos los comentarios de un usuario
    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // Eliminar todos los comentarios de eventos organizados por un usuario
    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.event.user.id = :organizerId")
    void deleteByEventOrganizerId(@Param("organizerId") Long organizerId);

    // Eliminar todos los comentarios de un evento
    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.event.id = :eventId")
    void deleteByEventId(@Param("eventId") Long eventId);
}