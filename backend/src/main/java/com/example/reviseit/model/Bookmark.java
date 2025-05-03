package com.example.reviseit.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Entity
@Data
public class Bookmark {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private String website; // Full URL

  private String domainName; // Domain name only

  private Float progress;

  @OneToMany(
    mappedBy = "bookmark",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<FlashCardSet> flashCardSet;
}
