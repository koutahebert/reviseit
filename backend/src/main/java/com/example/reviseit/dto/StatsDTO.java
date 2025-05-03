package com.example.reviseit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO {

  private long totalBookmarks;
  private long totalFlashcards;
  private long totalQuizQuestions;
}
