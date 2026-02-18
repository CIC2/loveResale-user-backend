package com.resale.resaleuser.repository;

import com.resale.resaleuser.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {

        @Query(value = """
            SELECT p.* 
            FROM permission p
            JOIN user_permission up ON up.permission_id = p.id
            WHERE up.user_id = :userId
            """, nativeQuery = true)
        Set<Permission> findPermissionsByUserId(Integer userId);

    Optional<Permission> findByActionAndResource(String action, String resource);

}


