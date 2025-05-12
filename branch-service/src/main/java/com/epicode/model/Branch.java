package com.epicode.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT '10030'")
    private String basic_cost = "10030";

    @Column
    private Boolean weekly_allowance;

    @Column(length = 2083)
    private String images;

    @Column
    private String contents;
}
