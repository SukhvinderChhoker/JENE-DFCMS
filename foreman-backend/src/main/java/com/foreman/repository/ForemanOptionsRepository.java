package com.foreman.repository;

import com.foreman.model.ForemanOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForemanOptionsRepository extends JpaRepository<ForemanOptions, Long> {
    Optional<ForemanOptions> findFirstByOrderByIdAsc();
}
