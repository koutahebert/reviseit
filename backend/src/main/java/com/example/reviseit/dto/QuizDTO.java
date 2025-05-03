package com.example.reviseit.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizDTO {
    private String name;
    private List<QuestionDTO> questions;
}
