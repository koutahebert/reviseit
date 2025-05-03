package com.example.reviseit.dto;

import java.util.List;
import lombok.Data;

@Data
public class BookmarkDTO {

  private Long id;
  private String website; // Full URL
  private String domainName; // Domain name only
  private Float progress;
  // Optionally include FlashCardSet info if needed by the frontend
  // private List<FlashCardSetDTO> flashCardSets;
}
