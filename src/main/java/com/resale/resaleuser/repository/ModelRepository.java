package com.resale.resaleuser.repository;

import com.resale.resaleuser.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Integer> {

    @Query("select m.id from Model m where m.projectId in :projectIds")
    List<Integer> findModelIdsByProjectIds(@Param("projectIds") List<Integer> projectIds);
}


