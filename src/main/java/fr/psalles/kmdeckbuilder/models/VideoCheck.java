package fr.psalles.kmdeckbuilder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoCheck {

    String type;
    boolean invalidFormat;
    boolean validId;
    String id;
}
