package com.foreman.repository;

import com.foreman.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByUserId(Long userId);
    List<Role> findByUserIdAndRemovedFalse(Long userId);
}
