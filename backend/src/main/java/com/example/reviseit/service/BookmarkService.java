package com.example.reviseit.service;

import com.example.reviseit.dto.BookmarkDTO;
import com.example.reviseit.dto.BookmarkDetailDTO;
import com.example.reviseit.dto.FlashCardSetDTO;
import com.example.reviseit.dto.StatsDTO; // Import StatsDTO
import com.example.reviseit.model.Bookmark;
import com.example.reviseit.model.FlashCardSet;
import com.example.reviseit.model.User;
import com.example.reviseit.repository.BookmarkRepository;
import com.example.reviseit.repository.FlashCardRepository; // Import FlashCardRepository
import com.example.reviseit.repository.QuestionRepository; // Import QuestionRepository
import com.example.reviseit.repository.UserRepository; // Assuming you have this repository
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections; // Import Collections
import java.util.List;
import java.util.Optional; // Import Optional
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;
  private final UserRepository userRepository; // Inject UserRepository
  private final FlashCardRepository flashCardRepository; // Inject FlashCardRepository
  private final QuestionRepository questionRepository; // Inject QuestionRepository

  // Method to extract domain name from URL
  private String extractDomainName(String url) {
    if (url == null || url.isEmpty()) {
      return null;
    }
    try {
      // Add scheme if missing for URI parsing
      String fullUrl = url;
      if (!url.matches("^\\w+://.*")) {
        fullUrl = "http://" + url; // Default to http if no scheme
      }
      URI uri = new URI(fullUrl);
      String domain = uri.getHost();
      // Remove www. prefix if present
      return domain == null
        ? null
        : domain.startsWith("www.") ? domain.substring(4) : domain;
    } catch (URISyntaxException e) {
      System.err.println(
        "Error parsing URL for domain extraction: " +
        url +
        " - " +
        e.getMessage()
      );
      // Fallback or return null/empty string based on requirements
      return null;
    }
  }

  // Convert Entity to DTO
  private BookmarkDTO convertToDTO(Bookmark bookmark) {
    BookmarkDTO dto = new BookmarkDTO();
    dto.setId(bookmark.getId());
    dto.setWebsite(bookmark.getWebsite());
    dto.setDomainName(bookmark.getDomainName());
    dto.setProgress(bookmark.getProgress());
    return dto;
  }

  // Convert FlashCardSet Entity to DTO
  private FlashCardSetDTO convertToFlashCardSetDTO(FlashCardSet flashCardSet) {
    FlashCardSetDTO dto = new FlashCardSetDTO();
    dto.setId(flashCardSet.getId());
    dto.setName(flashCardSet.getName());
    // Calculate and set the number of flashcards
    if (flashCardSet.getCards() != null) {
      dto.setNumberOfFlashcards(flashCardSet.getCards().size());
    } else {
      dto.setNumberOfFlashcards(0);
    }
    return dto;
  }

  // Convert Bookmark Entity to BookmarkDetailDTO
  private BookmarkDetailDTO convertToBookmarkDetailDTO(Bookmark bookmark) {
    BookmarkDetailDTO dto = new BookmarkDetailDTO();
    dto.setId(bookmark.getId());
    dto.setWebsite(bookmark.getWebsite());
    dto.setDomainName(bookmark.getDomainName());
    dto.setProgress(bookmark.getProgress());
    List<FlashCardSetDTO> flashCardSetDTOs = bookmark
      .getFlashCardSet()
      .stream()
      .map(this::convertToFlashCardSetDTO)
      .collect(Collectors.toList());
    dto.setFlashCardSets(flashCardSetDTOs);
    return dto;
  }

  public List<BookmarkDTO> getBookmarksByUserEmail(String email) {
    // Fetch bookmarks directly using the email.
    // If the user doesn't exist, this will return an empty list.
    List<Bookmark> bookmarks = bookmarkRepository.findByUserEmail(email);

    return bookmarks
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public BookmarkDTO addBookmark(BookmarkDTO bookmarkDTO, String email) {
    User user = userRepository
      .findByEmail(email)
      .orElseThrow(() ->
        new RuntimeException("User not found with email: " + email)
      );

    Bookmark bookmark = new Bookmark();
    bookmark.setUser(user);
    bookmark.setWebsite(bookmarkDTO.getWebsite());
    // Extract and set domain name
    bookmark.setDomainName(extractDomainName(bookmarkDTO.getWebsite()));
    // Set default progress or get from DTO if available
    bookmark.setProgress(
      bookmarkDTO.getProgress() != null ? bookmarkDTO.getProgress() : 0.0f
    );

    Bookmark savedBookmark = bookmarkRepository.save(bookmark);
    return convertToDTO(savedBookmark);
  }

  public void deleteBookmark(Long bookmarkId, String email) {
    // Find bookmark ensuring it belongs to the user making the request
    Bookmark bookmark = bookmarkRepository
      .findByIdAndUserEmail(bookmarkId, email)
      .orElseThrow(() ->
        new RuntimeException(
          "Bookmark not found or user does not have permission"
        )
      ); // Or a custom exception

    bookmarkRepository.delete(bookmark);
  }

  @Transactional(readOnly = true) // Use read-only transaction
  public Set<String> getUniqueDomainsByUserEmail(String email) {
    List<Bookmark> bookmarks = bookmarkRepository.findByUserEmail(email);
    return bookmarks
      .stream()
      .map(Bookmark::getDomainName)
      .filter(domain -> domain != null && !domain.isEmpty()) // Filter out null/empty domains
      .collect(Collectors.toSet());
  }

  @Transactional(readOnly = true)
  public List<BookmarkDTO> getBookmarksByDomain(
    String email,
    String domainName
  ) {
    List<Bookmark> bookmarks = bookmarkRepository.findByUserEmailAndDomainName(
      email,
      domainName
    );
    return bookmarks
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public BookmarkDetailDTO getBookmarkDetailsByWebsite(
    String email,
    String websiteUrl
  ) {
    // Find the specific bookmark for the user and website
    Bookmark bookmark = bookmarkRepository
      .findByUserEmailAndWebsite(email, websiteUrl)
      .orElseThrow(() ->
        new RuntimeException(
          "Bookmark not found for user " + email + " and website " + websiteUrl
        )
      ); // Use specific exception

    // Convert to the detailed DTO, which includes flashcard sets
    return convertToBookmarkDetailDTO(bookmark);
  }

  @Transactional(readOnly = true)
  public StatsDTO getUserStats(String email) {
    System.out.println(
      "BookmarkService.getUserStats: Fetching stats for user: " + email
    ); // Add log

    // Use the repository count methods
    long totalBookmarks = bookmarkRepository.countByUserEmail(email);
    long totalFlashcards = flashCardRepository.countByUserEmail(email);
    long totalQuizQuestions = questionRepository.countByUserEmail(email);

    System.out.println(
      "BookmarkService.getUserStats: Found stats - Bookmarks: " +
      totalBookmarks +
      ", Flashcards: " +
      totalFlashcards +
      ", Quizzes: " +
      totalQuizQuestions
    ); // Add log

    return new StatsDTO(totalBookmarks, totalFlashcards, totalQuizQuestions);
  }
}
