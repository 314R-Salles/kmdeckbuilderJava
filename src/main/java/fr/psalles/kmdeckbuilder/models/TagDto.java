package fr.psalles.kmdeckbuilder.models;

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
    private String frTitle;
    private String enTitle;
    private String esTitle;
    private String brTitle;
    private String ruTitle;
    private String iconId;
    private boolean disabled;
}
