package com.example.reviseit.repository;

import com.example.reviseit.model.QuizSet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuizSetRepository extends JpaRepository<QuizSet, Long> {
  // Find a quiz set by its ID and ensure it belongs to the user via FlashCardSet -> Bookmark -> User
  @Query(
    "SELECT qs FROM QuizSet qs JOIN FETCH qs.questions q JOIN FETCH q.choices c JOIN qs.flashCardSet fcs JOIN fcs.bookmark b JOIN b.user u WHERE qs.id = :quizSetId AND u.email = :email"
  )
  Optional<QuizSet> findByIdAndUserEmailWithDetails(
    @Param("quizSetId") Long quizSetId,
    @Param("email") String email
  );
}
