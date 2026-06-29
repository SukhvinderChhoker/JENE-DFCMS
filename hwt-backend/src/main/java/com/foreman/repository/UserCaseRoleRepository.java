package com.foreman.repository;

import com.foreman.model.UserCaseRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCaseRoleRepository extends JpaRepository<UserCaseRole, Long> {
    List<UserCaseRole> findByCaseEntityId(Long caseId);
    List<UserCaseRole> findByUserId(Long userId);
    List<UserCaseRole> findByCaseEntityIdAndRole(Long caseId, String role);
    UserCaseRole findByCaseEntityIdAndUserIdAndRole(Long caseId, Long userId, String role);
}
