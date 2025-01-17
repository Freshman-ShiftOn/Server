package com.epicode.manualservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@Table(name = "favorites")
@IdClass(FavoriteId.class)
@AllArgsConstructor
@NoArgsConstructor
public class Favorite {
    @Id
    private Long userId;

    @Id
    private Long branchId;

    @Id
    private Integer manualId;

    @Column(nullable = false)
    private boolean isFavorite;
}
