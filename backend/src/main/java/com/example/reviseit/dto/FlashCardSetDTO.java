package com.example.reviseit.dto;

import lombok.Data;

import java.util.List;

@Data
public class FlashCardSetDTO {
    private String name;
    private List<FlashCardDTO> cards;
}
