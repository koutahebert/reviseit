package com.example.reviseit.dto;

import java.util.List;
import lombok.Data;

@Data
public class FlashCardSetDTO {

  private Long id; // Add ID field
  private String name;
  private List<FlashCardDTO> cards;
  private Integer numberOfFlashcards; // Add count field
}
