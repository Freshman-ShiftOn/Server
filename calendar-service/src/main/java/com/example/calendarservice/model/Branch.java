package com.example.calendarservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "branches")
public class Branch {
    @Id
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;
}
