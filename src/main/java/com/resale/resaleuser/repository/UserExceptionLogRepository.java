package com.resale.resaleuser.repository;

import com.resale.resaleuser.model.UserExceptionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExceptionLogRepository extends JpaRepository<UserExceptionLog, Integer> {
}


