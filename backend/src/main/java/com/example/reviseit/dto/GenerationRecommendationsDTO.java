package com.example.reviseit.dto;

import lombok.Data;

@Data
public class GenerationRecommendationsDTO {
    private int flashcardCount;
    private int quizQuestionCount;
    public GenerationRecommendationsDTO(int flashcardCount, int quizQuestionCount) {
        this.flashcardCount = flashcardCount;
        this.quizQuestionCount = quizQuestionCount;
    }
}