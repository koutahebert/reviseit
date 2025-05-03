package com.example.reviseit.repository;

import com.example.reviseit.model.FlashCard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FlashCardRepository extends JpaRepository<FlashCard, Long> {
  // Find flashcards belonging to a specific set owned by a specific user
  @Query(
    "SELECT fc FROM FlashCard fc JOIN fc.flashCardSet fcs JOIN fcs.bookmark b JOIN b.user u WHERE fcs.id = :flashCardSetId AND u.email = :email"
  )
  List<FlashCard> findByFlashCardSetIdAndUserEmail(
    @Param("flashCardSetId") Long flashCardSetId,
    @Param("email") String email
  );

  // Count flashcards by user email
  @Query(
    "SELECT count(fc) FROM FlashCard fc JOIN fc.flashCardSet fcs JOIN fcs.bookmark b JOIN b.user u WHERE u.email = :email"
  )
  long countByUserEmail(@Param("email") String email);
}
