package com.example.reviseit.repository;

import com.example.reviseit.model.FlashCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashCardRepository extends JpaRepository<FlashCard, Long> {

}
