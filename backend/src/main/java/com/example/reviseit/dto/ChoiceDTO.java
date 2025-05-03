package com.example.reviseit.dto;

import lombok.Data;

@Data
public class ChoiceDTO {

  private Long id;
  private String text;
  private Boolean isCorrect;
}
