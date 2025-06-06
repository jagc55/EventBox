package com.eventbox.backend.service;

import com.eventbox.backend.model.Comment;
import com.eventbox.backend.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository repo;

    public CommentService(CommentRepository repo) {
        this.repo = repo;
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
}
