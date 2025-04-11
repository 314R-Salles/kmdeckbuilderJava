package fr.psalles.kmdeckbuilder.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HighlightDto {
    private int cardId;
    private int highlightOrder;
}
