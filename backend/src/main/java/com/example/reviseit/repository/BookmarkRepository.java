package com.example.reviseit.repository;

import com.example.reviseit.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByIdAndUserEmail(Long id, String email);
}
