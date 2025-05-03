package com.example.reviseit.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class FlashCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_card_set_id", nullable = false)
    private FlashCardSet flashCardSet;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;
}
