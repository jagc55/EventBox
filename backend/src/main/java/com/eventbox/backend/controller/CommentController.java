package com.eventbox.backend.controller;

import com.eventbox.backend.dto.CommentCreateDTO;
import com.eventbox.backend.model.Comment;
import com.eventbox.backend.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Comment> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Comment> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Comment save(@RequestBody Comment comment) {
        return service.save(comment);
    }

    @PostMapping("/create")
    public Comment createComment(@RequestBody CommentCreateDTO commentDTO) {
        return service.createComment(commentDTO);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Comentario eliminado correctamente";
    }

    @GetMapping("/event/{eventId}")
    public List<Comment> getCommentsByEvent(@PathVariable Long eventId) {
        return service.getCommentsByEvent(eventId);
    }
}
