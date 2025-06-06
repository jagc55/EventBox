package com.eventbox.backend.repository;

import com.eventbox.backend.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvinceRepository extends JpaRepository<Province, Long> {
}