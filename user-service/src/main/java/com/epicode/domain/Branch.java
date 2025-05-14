package com.epicode.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "branches")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(nullable = false)
    private String adress;

    @Column(nullable = true)
    private String dial_numbers;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT '10030'")
    private String basic_cost = "10030";

    @Column
    private Boolean weekly_allowance;

    @Column(nullable = true, length = 2083)
    private String images;

    @Column(nullable = true)
    private String contents;

    @Column(nullable = true)
    private String openTime;

    @Column(nullable = true)
    private String endTime;
}
