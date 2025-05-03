package com.example.reviseit.controller;

import com.example.reviseit.dto.GenerateRequestDTO;
import com.example.reviseit.dto.GenerationResponseDTO;
import com.example.reviseit.service.GenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/generate")
@RequiredArgsConstructor
public class GenerationController {

    private final GenerationService generationService;

    /**
     * Initiates the asynchronous generation of flashcards and quizzes for a given bookmark.
     *
     * @param bookmarkId The ID of the bookmark to generate content for.
     * @param request    The DTO containing the source text blocks and desired names for the sets.
     * @return A DTO containing the IDs of the newly created (but initially empty) FlashCardSet and QuizSet.
     */
    @PostMapping("/{bookmarkId}/init")
    public ResponseEntity<GenerationResponseDTO> initiateGeneration(
            @PathVariable Long bookmarkId,
            @RequestBody GenerateRequestDTO request) {
        GenerationResponseDTO response = generationService.initAndQueue(bookmarkId, request);
        // Consider returning 202 Accepted as the process is async
        return ResponseEntity.accepted().body(response);
    }

}
