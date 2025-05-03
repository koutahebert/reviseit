package com.example.reviseit.controller; // Assuming you have a controller package

import com.example.reviseit.dto.BookmarkDTO;
import com.example.reviseit.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<BookmarkDTO>> getUserBookmarks(@AuthenticationPrincipal OAuth2User principal) {
        String email = getUserEmail(principal);
        List<BookmarkDTO> bookmarks = bookmarkService.getBookmarksByUserEmail(email);
        return ResponseEntity.ok(bookmarks);
    }

    @PostMapping
    public ResponseEntity<BookmarkDTO> addBookmark(@RequestBody BookmarkDTO bookmarkDTO, @AuthenticationPrincipal OAuth2User principal) {
        String email = getUserEmail(principal);
        BookmarkDTO createdBookmark = bookmarkService.addBookmark(bookmarkDTO, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBookmark);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        String email = getUserEmail(principal);
        bookmarkService.deleteBookmark(id, email);
        return ResponseEntity.noContent().build();
    }
}
