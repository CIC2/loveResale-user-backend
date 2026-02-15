package com.resale.homeflyuser.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer actionCode;
    private String actionName;
    private Integer userId;
    private String httpMethod;
    private Integer statusCode;

    @Column(columnDefinition = "TEXT")
    private String headers;

    @Column(columnDefinition = "TEXT")
    private String queryParams;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    private Long executionTimeMs;

    private LocalDateTime createdAt = LocalDateTime.now();
}


