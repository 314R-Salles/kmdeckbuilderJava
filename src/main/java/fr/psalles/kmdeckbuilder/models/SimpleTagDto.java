package fr.psalles.kmdeckbuilder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleTagDto {
    private Integer id;
    private String title;
    private String iconId;
    private Integer count;
    private boolean disabled;
}
