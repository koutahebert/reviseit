package com.example.reviseit.repository;

import com.example.reviseit.model.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
  Optional<Bookmark> findByIdAndUserEmail(Long id, String email);

  // Find all bookmarks for a given user's email
  List<Bookmark> findByUserEmail(String email);

  // Find bookmarks by user email and domain name
  List<Bookmark> findByUserEmailAndDomainName(String email, String domainName);

  // Find a bookmark by user email and website URL
  Optional<Bookmark> findByUserEmailAndWebsite(String email, String website);
}
