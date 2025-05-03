package com.example.reviseit.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class GenerateRequestDTO {

  @NotEmpty(message = "Source text blocks cannot be empty")
  private List<String> sourceBlocks; // Renamed from chunks, added validation

  @NotNull(message = "Flashcard set name cannot be null")
  @NotEmpty(message = "Flashcard set name cannot be empty")
  private String flashcardSetName;

  @NotNull(message = "Quiz set name cannot be null")
  @NotEmpty(message = "Quiz set name cannot be empty")
  private String quizSetName;
}
