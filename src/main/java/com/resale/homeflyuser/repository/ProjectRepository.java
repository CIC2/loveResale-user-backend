package com.resale.homeflyuser.repository;

import com.resale.homeflyuser.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
}


