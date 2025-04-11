package fr.psalles.kmdeckbuilder.models;

import fr.psalles.kmdeckbuilder.models.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDto {
    private Integer id;
    private Language language;
    private String name;
    private String iconId;
    private boolean disabled;
}
