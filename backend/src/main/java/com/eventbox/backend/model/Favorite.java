package com.eventbox.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@IdClass(FavoriteId.class)
@Table(name = "Favorite")
public class Favorite {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    public Favorite() {}

    public Favorite(User user, Event event, LocalDateTime addedAt) {
        this.user = user;
        this.event = event;
        this.addedAt = addedAt;
    }

    // Getters y Setters

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
