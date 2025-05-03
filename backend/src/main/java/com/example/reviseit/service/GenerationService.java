package com.example.reviseit.service;

import com.example.reviseit.dto.*;
import com.example.reviseit.model.*;
import com.example.reviseit.repository.BookmarkRepository;
import com.example.reviseit.repository.FlashCardSetRepository;
import com.example.reviseit.repository.QuizSetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GenerationService {

  private static final Logger log = LoggerFactory.getLogger(
    GenerationService.class
  );
  private final BookmarkRepository bookmarkRepo;
  private final FlashCardSetRepository flashSetRepo;
  private final QuizSetRepository quizSetRepo;
  private final ObjectMapper mapper = new ObjectMapper();
  private WebClient geminiClient; // Initialized in init()

  @Value("${ai.gemini.url}")
  private String geminiUrl;

  @Value("${ai.gemini.api-key}")
  private String geminiApiKey; // Assuming API key is needed

  public GenerationService(
    BookmarkRepository bookmarkRepo,
    FlashCardSetRepository flashSetRepo,
    QuizSetRepository quizSetRepo
  ) {
    this.bookmarkRepo = bookmarkRepo;
    this.flashSetRepo = flashSetRepo;
    this.quizSetRepo = quizSetRepo;
  }

  @PostConstruct
  private void init() {
    // Initialize WebClient here after properties are injected
    this.geminiClient =
      WebClient
        .builder()
        .baseUrl(geminiUrl)
        // Add authentication header if needed, e.g.:
        // .defaultHeader("Authorization", "Bearer " + geminiApiKey)
        .build();
    log.info("Gemini WebClient initialized for URL: {}", geminiUrl);
    if (
      geminiApiKey == null ||
      geminiApiKey.isBlank() ||
      geminiApiKey.equals("YOUR_API_KEY_HERE")
    ) {
      log.warn(
        "AI API Key is not configured in application.properties. AI generation might fail."
      );
    }
  }

  @Transactional
  public GenerationResponseDTO initAndQueue(
    Long bookmarkId,
    GenerateRequestDTO req
  ) {
    Bookmark bm = bookmarkRepo
      .findById(bookmarkId)
      .orElseThrow(() ->
        new IllegalArgumentException("Invalid bookmark ID: " + bookmarkId)
      );

    // Persist empty containers immediately
    FlashCardSet flashSet = new FlashCardSet();
    flashSet.setBookmark(bm);
    flashSet = flashSetRepo.save(flashSet);
    log.info("Created initial FlashCardSet with ID: {}", flashSet.getId());

    QuizSet quizSet = new QuizSet();
    quizSet.setFlashCardSet(flashSet);
    quizSet = quizSetRepo.save(quizSet);
    log.info("Created initial QuizSet with ID: {}", quizSet.getId());

    // Async generation with provided text - Counts removed
    generateFlashcards(flashSet.getId(), req.getSourceBlocks());
    generateQuiz(quizSet.getId(), req.getSourceBlocks());

    return new GenerationResponseDTO(flashSet.getId(), quizSet.getId());
  }

  @Async
  void generateFlashcards(Long flashSetId, List<String> sourceBlocks) {
    log.info("Starting flashcard generation for set ID: {}", flashSetId);
    try {
      String sourceText = String.join("\n\n", sourceBlocks); // Use single backslash for newline in join

      // Use text block for JSON example format
      String flashcardJsonFormatExample =
        """
            {
              "name": "Generated Flashcards",
              "cards": [
                {
                  "question": "Question text 1",
                  "answer": "Answer text 1"
                },
                {
                  "question": "Question text 2",
                  "answer": "Answer text 2"
                }
              ]
            }"""; // Note: The AI should generate 'count' cards, this is just an example structure.

      // Modified prompt: Ask LLM to decide the count
      String prompt = String.format(
        """
                Based on the following text, generate an appropriate number of flashcards suitable for the content length and detail.
                Format the output strictly as JSON, matching this structure:
                %s

                Text:
                %s
                """,
        flashcardJsonFormatExample,
        sourceText
      );
      log.info("Flashcard Generation Prompt:\\n---\\n{}\\n---", prompt); // Added logging

      // Use the initialized geminiClient
      String res = geminiClient
        .post()
        .uri(uriBuilder -> uriBuilder.queryParam("key", geminiApiKey).build()) // Add API key as query param
        .contentType(MediaType.APPLICATION_JSON) // Set content type
        .bodyValue(
          Map.of(
            "contents",
            List.of(Map.of("parts", List.of(Map.of("text", prompt))))
          )
        ) // Gemini API structure
        .retrieve()
        .bodyToMono(String.class)
        // Add error handling for WebClient request
        .doOnError(error ->
          log.error(
            "Error calling AI service for flashcards (Set ID: {}): {}",
            flashSetId,
            error.getMessage()
          )
        )
        .onErrorResume(error -> Mono.empty()) // Prevent failure from stopping execution, return empty Mono
        .block(); // block() is generally discouraged in reactive flows, consider returning Mono<Void>

      if (res == null || res.isBlank()) {
        log.error(
          "Received empty response from AI service for flashcards (Set ID: {})",
          flashSetId
        );
        // Optionally update FlashCardSet status to indicate failure
        return;
      }

      log.debug(
        "Raw AI response for flashcards (Set ID: {}): {}",
        flashSetId,
        res
      );

      // Attempt to parse the response
      FlashCardSetDTO dto = mapper.readValue(res, FlashCardSetDTO.class);

      // Use orElseThrow for safer entity retrieval
      FlashCardSet flashSet = flashSetRepo
        .findById(flashSetId)
        .orElseThrow(() ->
          new IllegalStateException(
            "FlashCardSet not found during async update: " + flashSetId
          )
        );

      flashSet.setName(dto.getName()); // Update name from AI response
      flashSet.getCards().clear(); // Clear any potential placeholders if needed
      flashSet
        .getCards()
        .addAll(
          dto
            .getCards()
            .stream()
            .map(cdto -> {
              FlashCard card = new FlashCard();
              card.setQuestion(cdto.getQuestion());
              card.setAnswer(cdto.getAnswer());
              card.setFlashCardSet(flashSet); // Set bidirectional relationship
              return card;
            })
            .toList()
        );
      flashSetRepo.save(flashSet);
      log.info(
        "Successfully generated and saved {} flashcards for set ID: {}",
        flashSet.getCards().size(),
        flashSetId
      );
    } catch (Exception e) {
      // Log detailed error
      log.error(
        "Failed to generate flashcards for set ID: {}. Error: {}",
        flashSetId,
        e.getMessage(),
        e
      );
      // Optionally: Update the FlashCardSet entity to indicate failure status
    }
  }

  @Async
  void generateQuiz(Long quizSetId, List<String> sourceBlocks) {
    log.info("Starting quiz generation for set ID: {}", quizSetId);
    try {
      String sourceText = String.join("\n\n", sourceBlocks); // Use single backslash for newline in join

      // Use text block for JSON example format
      String quizJsonFormatExample =
        """
            {
              "name": "Generated Quiz",
              "questions": [
                {
                  "text": "Question 1 text",
                  "choices": [
                    { "text": "Choice A", "isCorrect": false },
                    { "text": "Choice B", "isCorrect": true },
                    { "text": "Choice C", "isCorrect": false }
                  ]
                },
                {
                  "text": "Question 2 text",
                  "choices": [
                    { "text": "Option 1", "isCorrect": true },
                    { "text": "Option 2", "isCorrect": false }
                  ]
                }
              ]
            }"""; // Note: The AI should generate 'count' questions, this is just an example structure.

      // Modified prompt: Ask LLM to decide the count
      String prompt = String.format(
        """
                Based on the following text, generate an appropriate number of multiple-choice quiz questions suitable for the content length and detail.
                Each question must have at least one correct answer marked with "isCorrect": true.
                Format the output strictly as JSON, matching this structure:
                %s

                Text:
                %s
                """,
        quizJsonFormatExample,
        sourceText
      );
      log.info("Quiz Generation Prompt:\\n---\\n{}\\n---", prompt); // Added logging

      // Use the initialized geminiClient
      String res = geminiClient
        .post()
        .uri(uriBuilder -> uriBuilder.queryParam("key", geminiApiKey).build()) // Add API key as query param
        .contentType(MediaType.APPLICATION_JSON) // Set content type
        .bodyValue(
          Map.of(
            "contents",
            List.of(Map.of("parts", List.of(Map.of("text", prompt))))
          )
        ) // Gemini API structure
        .retrieve()
        .bodyToMono(String.class)
        // Add error handling for WebClient request
        .doOnError(error ->
          log.error(
            "Error calling AI service for quiz (Set ID: {}): {}",
            quizSetId,
            error.getMessage()
          )
        )
        .onErrorResume(error -> Mono.empty()) // Prevent failure from stopping execution, return empty Mono
        .block(); // block() is generally discouraged in reactive flows, consider returning Mono<Void>

      if (res == null || res.isBlank()) {
        log.error(
          "Received empty response from AI service for quiz (Set ID: {})",
          quizSetId
        );
        // Optionally update QuizSet status to indicate failure
        return;
      }

      log.debug("Raw AI response for quiz (Set ID: {}): {}", quizSetId, res);

      // Attempt to parse the response
      QuizDTO dto = mapper.readValue(res, QuizDTO.class);

      // Use orElseThrow for safer entity retrieval
      QuizSet quizSet = quizSetRepo
        .findById(quizSetId)
        .orElseThrow(() ->
          new IllegalStateException(
            "QuizSet not found during async update: " + quizSetId
          )
        );

      quizSet.setName(dto.getName()); // Update name from AI response
      quizSet.getQuestions().clear(); // Clear any potential placeholders if needed
      quizSet
        .getQuestions()
        .addAll(
          dto
            .getQuestions()
            .stream()
            .map(qdto -> {
              Question q = new Question();
              q.setText(qdto.getText());
              q.setQuizSet(quizSet); // Set bidirectional relationship

              q.setChoices(
                qdto
                  .getChoices()
                  .stream()
                  .map(cdto -> {
                    Choice c = new Choice();
                    c.setText(cdto.getText());
                    c.setIsCorrect(cdto.getIsCorrect());
                    c.setQuestion(q); // Set bidirectional relationship
                    return c;
                  })
                  .collect(Collectors.toList())
              );
              return q;
            })
            .collect(Collectors.toList())
        );
      quizSetRepo.save(quizSet);
      log.info(
        "Successfully generated and saved {} questions for quiz set ID: {}",
        quizSet.getQuestions().size(),
        quizSetId
      );
    } catch (Exception e) {
      // Log detailed error
      log.error(
        "Failed to generate quiz for set ID: {}. Error: {}",
        quizSetId,
        e.getMessage(),
        e
      );
      // Optionally: Update the QuizSet entity to indicate failure status
    }
  }
}
