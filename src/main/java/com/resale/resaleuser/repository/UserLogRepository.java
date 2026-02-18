package com.resale.resaleuser.repository;

import com.resale.resaleuser.model.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository  extends JpaRepository<UserLog, Integer> {
}


