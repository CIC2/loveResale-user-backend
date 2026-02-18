package com.resale.resaleuser.repository;

import com.resale.resaleuser.model.UserLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLanguageRepository extends JpaRepository<UserLanguage, Integer> {
    List<UserLanguage> findByUserId(Integer userId);
    void deleteByUserId(Integer userId);

}


