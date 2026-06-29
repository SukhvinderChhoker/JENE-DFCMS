package com.foreman.repository;

import com.foreman.model.UserTaskRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTaskRoleRepository extends JpaRepository<UserTaskRole, Long> {
    List<UserTaskRole> findByTaskId(Long taskId);
    List<UserTaskRole> findByUserId(Long userId);
    List<UserTaskRole> findByTaskIdAndRole(Long taskId, String role);
    UserTaskRole findByTaskIdAndUserIdAndRole(Long taskId, Long userId, String role);
}
