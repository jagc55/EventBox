package com.eventbox.backend.repository;

import com.eventbox.backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventIdOrderByDateDesc(Long eventId);
}