package com.epicode.domain;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@Entity
@Table(name = "bosses")
public class Boss {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    @OneToMany(mappedBy = "boss", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<UserBranch> userBranches;
}
