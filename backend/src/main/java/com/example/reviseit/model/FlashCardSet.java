package com.example.reviseit.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@Entity
public class FlashCardSet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bookmark_id", nullable = false)
  private Bookmark bookmark;

  private String name;

  @OneToMany(
    mappedBy = "flashCardSet",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private List<FlashCard> cards = new ArrayList<>();
}
