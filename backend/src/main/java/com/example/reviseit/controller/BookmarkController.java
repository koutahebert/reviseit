package com.example.reviseit.controller; // Assuming you have a controller package

import com.example.reviseit.dto.BookmarkDTO;
import com.example.reviseit.dto.BookmarkDetailDTO;
import com.example.reviseit.dto.StatsDTO; // Import StatsDTO
import com.example.reviseit.service.BookmarkService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

  private final BookmarkService bookmarkService;

  // Helper to get email from authenticated principal
  private String getUserEmail(OAuth2User principal) {
    if (principal == null) {
      // Handle cases where the user is not authenticated if necessary,
      // though Spring Security should protect the endpoints
      throw new IllegalStateException("User not authenticated");
    }
    // Adjust attribute name based on your OAuth provider (e.g., "email")
    Map<String, Object> attributes = principal.getAttributes();
    return (String) attributes.get("email");
  }

  @GetMapping
  public ResponseEntity<List<BookmarkDTO>> getUserBookmarks(
    @AuthenticationPrincipal OAuth2User principal
  ) {
    String email = getUserEmail(principal);
    List<BookmarkDTO> bookmarks = bookmarkService.getBookmarksByUserEmail(
      email
    );
    return ResponseEntity.ok(bookmarks);
  }

  @PostMapping
  public ResponseEntity<BookmarkDTO> addBookmark(
    @RequestBody BookmarkDTO bookmarkDTO,
    @AuthenticationPrincipal OAuth2User principal
  ) {
    String email = getUserEmail(principal);
    BookmarkDTO createdBookmark = bookmarkService.addBookmark(
      bookmarkDTO,
      email
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(createdBookmark);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBookmark(
    @PathVariable Long id,
    @AuthenticationPrincipal OAuth2User principal
  ) {
    String email = getUserEmail(principal);
    bookmarkService.deleteBookmark(id, email);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/domains")
  public ResponseEntity<Set<String>> getUserDomains(
    @AuthenticationPrincipal OAuth2User principal
  ) {
    String email = getUserEmail(principal);
    Set<String> domains = bookmarkService.getUniqueDomainsByUserEmail(email);
    return ResponseEntity.ok(domains);
  }

  @GetMapping("/domain/{domainName}")
  public ResponseEntity<List<BookmarkDTO>> getBookmarksByDomain(
    @PathVariable String domainName,
    @AuthenticationPrincipal OAuth2User principal
  ) {
    String email = getUserEmail(principal);
    List<BookmarkDTO> bookmarks = bookmarkService.getBookmarksByDomain(
      email,
      domainName
    );
    return ResponseEntity.ok(bookmarks);
  }

  @GetMapping("/website")
  public ResponseEntity<BookmarkDetailDTO> getBookmarkDetailsByWebsite(
    @RequestParam String url, // Get website URL from request parameter
    @AuthenticationPrincipal OAuth2User principal
  ) {
    String email = getUserEmail(principal);
    BookmarkDetailDTO bookmarkDetail = bookmarkService.getBookmarkDetailsByWebsite(
      email,
      url
    );
    return ResponseEntity.ok(bookmarkDetail);
  }

  // Endpoint to get user statistics
  @GetMapping("/stats")
  public ResponseEntity<StatsDTO> getUserStats(
    @AuthenticationPrincipal OAuth2User principal
  ) {
    String email = getUserEmail(principal);
    StatsDTO stats = bookmarkService.getUserStats(email);
    return ResponseEntity.ok(stats);
  }
}
