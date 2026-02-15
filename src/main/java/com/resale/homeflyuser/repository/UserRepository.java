package com.resale.homeflyuser.repository;

import com.resale.homeflyuser.model.Role;
import com.resale.homeflyuser.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByFullName(String fullName);
    List<User> findAllByUserId(Integer userId);

    @Query("SELECT u FROM User u WHERE u.role = 'SALESMAN' ORDER BY u.lastAssigned ASC NULLS FIRST")
    List<User> findSalesmenOrderedByLastAssigned();

    @Query("""
    SELECT u
    FROM User u
    JOIN UserProject up ON up.userId = u.id
    WHERE u.role = 'SALESMAN'
      AND up.projectId = :projectId
    ORDER BY u.lastAssigned ASC NULLS FIRST
""")
    List<User> findSalesmenOrderedByLastAssigned(@Param("projectId") Integer projectId);

    List<User> findByRoleIn(List<Role> roles);
    boolean existsByIdAndUserId(Integer userId, Integer teamLeadId);

}


