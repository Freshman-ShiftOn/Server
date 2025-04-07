package com.epicode.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "boss_branch")
public class BossBranch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "boss_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Boss boss;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt = LocalDateTime.now();
}
