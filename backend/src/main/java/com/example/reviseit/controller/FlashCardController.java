package com.example.reviseit.controller;

import com.example.reviseit.dto.FlashCardDTO;
import com.example.reviseit.service.FlashCardService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flashcards")
@RequiredArgsConstructor
public class FlashCardController {

  private final FlashCardService flashCardService;

  // Helper to get email from authenticated principal (Consider moving to a utility class)
  private String getUserEmail(OAuth2User principal) {
    if (principal == null) {
      throw new IllegalStateException("User not authenticated");
    }
    Map<String, Object> attributes = principal.getAttributes();
    return (String) attributes.get("email");
  }

  @GetMapping("/set/{flashCardSetId}")
  public ResponseEntity<List<FlashCardDTO>> getFlashCardsBySet(
    @PathVariable Long flashCardSetId,
    @AuthenticationPrincipal OAuth2User principal
  ) {
    String email = getUserEmail(principal);
    List<FlashCardDTO> flashCards = flashCardService.getFlashCardsBySetId(
      flashCardSetId,
      email
    );
    return ResponseEntity.ok(flashCards);
  }
  // Add endpoints for POST, PUT, DELETE flashcards if needed
}
