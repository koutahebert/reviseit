package com.example.reviseit.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Data
public class Question {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quiz_set_id", nullable = false)
  private QuizSet quizSet;

  @Column(columnDefinition = "TEXT")
  private String text;

  @OneToMany(
    mappedBy = "question",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<Choice> choices = new ArrayList<>();
}
