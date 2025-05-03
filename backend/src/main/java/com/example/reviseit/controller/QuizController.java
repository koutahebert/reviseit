package com.example.reviseit.controller;

import com.example.reviseit.dto.QuizSetDTO;
import com.example.reviseit.service.QuizService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

  private final QuizService quizService;

  // Helper to get email from authenticated principal (Consider moving to a utility class)
  private String getUserEmail(OAuth2User principal) {
    if (principal == null) {
      throw new IllegalStateException("User not authenticated");
    }
    Map<String, Object> attributes = principal.getAttributes();
    return (String) attributes.get("email");
  }

  @GetMapping("/set/{quizSetId}")
  public ResponseEntity<QuizSetDTO> getQuizSet(
    @PathVariable Long quizSetId,
    @AuthenticationPrincipal OAuth2User principal
  ) {
    String email = getUserEmail(principal);
    QuizSetDTO quizSet = quizService.getQuizSetById(quizSetId, email);
    return ResponseEntity.ok(quizSet);
  }
  // Add endpoints for POST, PUT, DELETE quizzes if needed
}
