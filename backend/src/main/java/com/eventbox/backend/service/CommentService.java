package com.eventbox.backend.service;

import com.eventbox.backend.dto.CommentCreateDTO;
import com.eventbox.backend.model.Comment;
import com.eventbox.backend.model.Event;
import com.eventbox.backend.model.User;
import com.eventbox.backend.repository.CommentRepository;
import com.eventbox.backend.repository.EventRepository;
import com.eventbox.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository repo;
    private final UserRepository userRepo;
    private final EventRepository eventRepo;

    public CommentService(CommentRepository repo, UserRepository userRepo, EventRepository eventRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.eventRepo = eventRepo;
    }

    public List<Comment> getAll() {
        return repo.findAll();
    }

    public Optional<Comment> getById(Long id) {
        return repo.findById(id);
    }

    public Comment save(Comment comment) {
        return repo.save(comment);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Comment> getCommentsByEvent(Long eventId) {
        return repo.findByEventIdOrderByDateDesc(eventId);
    }

    public Comment createComment(CommentCreateDTO commentDTO) {
        // Buscar usuario y evento
        User user = userRepo.findById(commentDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Event event = eventRepo.findById(commentDTO.getEventId())
            .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Crear el comentario
        Comment comment = new Comment();
        comment.setDescription(commentDTO.getDescription());
        comment.setDate(LocalDateTime.now());
        comment.setUser(user);
        comment.setEvent(event);

        return repo.save(comment);
    }
}
