package fr.psalles.kmdeckbuilder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class News {
    private Long id; // pour redirection
    private String title;
    private String content;
    private String illustrationId;
    private String illustration;
    private boolean disabled;
}
