package com.example.reviseit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Keep default constructor if needed elsewhere
@AllArgsConstructor // Add constructor for all fields
public class GenerationResponseDTO {

  private Long flashCardSetId;
  private Long quizSetId;
}
