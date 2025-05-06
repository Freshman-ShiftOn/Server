package com.epicode.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_branch")
public class UserBranch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column
    private String roles;

    @Column
    private String personalCost;

    @Column
    private String status;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt = LocalDateTime.now();
}
