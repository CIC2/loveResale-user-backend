package com.resale.homeflyuser.repository;

import com.resale.homeflyuser.model.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository  extends JpaRepository<UserLog, Integer> {
}


