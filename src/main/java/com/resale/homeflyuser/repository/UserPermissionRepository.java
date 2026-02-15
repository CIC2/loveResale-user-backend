package com.resale.homeflyuser.repository;

import com.resale.homeflyuser.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Integer> {
    Set<UserPermission> findByUserId(Integer userId);
    void deleteByUserId(Integer userId);
}


