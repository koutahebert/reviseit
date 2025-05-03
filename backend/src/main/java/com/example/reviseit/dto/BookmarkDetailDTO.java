package com.example.reviseit.dto;

import java.util.List;
import lombok.Data;

@Data
public class BookmarkDetailDTO {

  private Long id;
  private String website;
  private String domainName;
  private Float progress;
  private List<FlashCardSetDTO> flashCardSets; // Include list of flashcard sets
}
