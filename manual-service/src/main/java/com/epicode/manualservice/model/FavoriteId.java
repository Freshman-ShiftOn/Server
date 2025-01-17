package com.epicode.manualservice.model;
import lombok.Data;
import java.io.Serializable;

@Data
public class FavoriteId implements Serializable {
    private Long userId;
    private Long branchId;
    private Integer manualId;
}