package com.resale.resaleuser.repository;

import com.resale.resaleuser.model.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, Integer> {
    List<UserProject> findByUserId(Integer userId);
    void deleteByUserId(Integer userId);

}


