package com.eventbox.backend.dto;

public class CommentCreateDTO {
    private String description;
    private Long userId;
    private Long eventId;

    public CommentCreateDTO() {}

    public CommentCreateDTO(String description, Long userId, Long eventId) {
        this.description = description;
        this.userId = userId;
        this.eventId = eventId;
    }

    // Getters y Setters

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
