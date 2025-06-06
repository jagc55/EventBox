package com.eventbox.backend.repository;

import com.eventbox.backend.model.Favorite;
import com.eventbox.backend.model.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
}