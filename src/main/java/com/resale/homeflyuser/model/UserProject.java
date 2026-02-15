package com.resale.homeflyuser.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;
}


