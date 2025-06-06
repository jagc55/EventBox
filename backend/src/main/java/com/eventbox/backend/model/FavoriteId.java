package com.eventbox.backend.model;

import java.io.Serializable;
import java.util.Objects;

public class FavoriteId implements Serializable {

    private Long user;
    private Long event;

    public FavoriteId() {}

    public FavoriteId(Long user, Long event) {
        this.user = user;
        this.event = event;
    }

    // equals() y hashCode()

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoriteId)) return false;
        FavoriteId that = (FavoriteId) o;
        return Objects.equals(user, that.user) && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, event);
    }
}
