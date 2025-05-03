package com.example.reviseit.repository;

import com.example.reviseit.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Long> {
  // Count questions by user email
  @Query(
    "SELECT count(q) FROM Question q JOIN q.quizSet qs JOIN qs.flashCardSet fcs JOIN fcs.bookmark b JOIN b.user u WHERE u.email = :email"
  )
  long countByUserEmail(@Param("email") String email);
}
