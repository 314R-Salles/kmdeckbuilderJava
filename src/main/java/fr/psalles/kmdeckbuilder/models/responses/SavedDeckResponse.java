package fr.psalles.kmdeckbuilder.models.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SavedDeckResponse {
    private String deckId;
    private int version;

}
