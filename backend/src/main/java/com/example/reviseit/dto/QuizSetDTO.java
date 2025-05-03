package com.example.reviseit.dto;

import java.util.List;
import lombok.Data;

@Data
public class QuizSetDTO {

  private Long id;
  private String name;
  private List<QuestionDTO> questions;
}
