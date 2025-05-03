package com.example.reviseit.service;

import com.example.reviseit.dto.FlashCardDTO;
import com.example.reviseit.model.FlashCard;
import com.example.reviseit.repository.FlashCardRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FlashCardService {

  private final FlashCardRepository flashCardRepository;

  // Convert FlashCard Entity to DTO
  private FlashCardDTO convertToDTO(FlashCard flashCard) {
    FlashCardDTO dto = new FlashCardDTO();
    dto.setId(flashCard.getId());
    dto.setQuestion(flashCard.getQuestion());
    dto.setAnswer(flashCard.getAnswer());
    return dto;
  }

  @Transactional(readOnly = true)
  public List<FlashCardDTO> getFlashCardsBySetId(
    Long flashCardSetId,
    String email
  ) {
    List<FlashCard> flashCards = flashCardRepository.findByFlashCardSetIdAndUserEmail(
      flashCardSetId,
      email
    );
    if (flashCards.isEmpty()) {
      // Optional: Check if the FlashCardSet itself exists for the user to give a more specific error
      // This would require injecting FlashCardSetRepository and adding a check.
      // For now, just return empty list or throw a generic not found if preferred.
    }
    return flashCards
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }
  // Add methods for creating, updating, deleting flashcards if needed
}
