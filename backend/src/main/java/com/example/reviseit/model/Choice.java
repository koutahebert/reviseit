package com.example.reviseit.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String text;
    private Boolean isCorrect;
}
