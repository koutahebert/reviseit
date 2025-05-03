package com.example.reviseit.dto;

import java.util.List;
import lombok.Data;

@Data
public class QuestionDTO {

  private Long id;
  private String text;
  private List<ChoiceDTO> choices;
}
