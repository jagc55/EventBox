package com.eventbox.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "EventType")
public class EventType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    public EventType() {}

    public EventType(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
