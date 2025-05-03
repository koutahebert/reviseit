package com.example.reviseit.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDTO {
    private String text;
    private List<ChoiceDTO> choices;
}
