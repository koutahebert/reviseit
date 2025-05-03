package com.example.reviseit.dto;

import lombok.Data;

@Data
public class FlashCardDTO {

  private Long id; // Add ID field
  private String question;
  private String answer;
}
