package com.eventbox.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_follows", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"}))
public class UserFollow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Constructores
    public UserFollow() {}
    
    public UserFollow(User follower, User followed) {
        this.follower = follower;
        this.followed = followed;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getFollower() {
        return follower;
    }
    
    public void setFollower(User follower) {
        this.follower = follower;
    }
    
    public User getFollowed() {
        return followed;
    }
    
    public void setFollowed(User followed) {
        this.followed = followed;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
