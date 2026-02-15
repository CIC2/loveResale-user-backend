package com.resale.homeflyuser.repository;

import com.resale.homeflyuser.model.UserExceptionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExceptionLogRepository extends JpaRepository<UserExceptionLog, Integer> {
}


