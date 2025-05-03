package com.example.reviseit.service;

import com.example.reviseit.dto.ChoiceDTO;
import com.example.reviseit.dto.QuestionDTO;
import com.example.reviseit.dto.QuizSetDTO;
import com.example.reviseit.model.Choice;
import com.example.reviseit.model.Question;
import com.example.reviseit.model.QuizSet;
import com.example.reviseit.repository.QuizSetRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Default to read-only for this service
public class QuizService {

  private final QuizSetRepository quizSetRepository;

  // --- DTO Conversion Methods ---

  private ChoiceDTO convertToChoiceDTO(Choice choice) {
    ChoiceDTO dto = new ChoiceDTO();
    dto.setId(choice.getId());
    dto.setText(choice.getText());
    dto.setIsCorrect(choice.getIsCorrect());
    return dto;
  }

  private QuestionDTO convertToQuestionDTO(Question question) {
    QuestionDTO dto = new QuestionDTO();
    dto.setId(question.getId());
    dto.setText(question.getText());
    // Convert choices associated with the question
    List<ChoiceDTO> choiceDTOs = question
      .getChoices()
      .stream()
      .map(this::convertToChoiceDTO)
      .collect(Collectors.toList());
    dto.setChoices(choiceDTOs);
    return dto;
  }

  private QuizSetDTO convertToQuizSetDTO(QuizSet quizSet) {
    QuizSetDTO dto = new QuizSetDTO();
    dto.setId(quizSet.getId());
    dto.setName(quizSet.getName());
    // Convert questions associated with the quiz set
    List<QuestionDTO> questionDTOs = quizSet
      .getQuestions()
      .stream()
      .map(this::convertToQuestionDTO)
      .collect(Collectors.toList());
    dto.setQuestions(questionDTOs);
    return dto;
  }

  // --- Service Methods ---

  public QuizSetDTO getQuizSetById(Long quizSetId, String email) {
    QuizSet quizSet = quizSetRepository
      .findByIdAndUserEmailWithDetails(quizSetId, email)
      .orElseThrow(() ->
        new RuntimeException(
          "QuizSet not found with id: " + quizSetId + " for user " + email
        )
      ); // Use specific exception

    return convertToQuizSetDTO(quizSet);
  }
  // Add methods for creating, updating, deleting quizzes if needed
}
