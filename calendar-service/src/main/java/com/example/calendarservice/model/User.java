package com.example.calendarservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;
}