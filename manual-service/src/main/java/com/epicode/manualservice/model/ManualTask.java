package com.epicode.manualservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(name = "manual_task")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ManualTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "manual_id", nullable = false)
    private Manual manual;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "task_no", nullable = false)
    private Integer taskNo;

    @Column(name = "image_url")
    private String imageUrl;
}