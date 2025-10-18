package fr.psalles.kmdeckbuilder.models.extern.auth0;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {

    String id;
    String type; // "verification_email",
    String status; // "pending",
    // https://stackoverflow.com/questions/27952472/serialize-deserialize-java-8-java-time-with-jackson-json-mapper
    String created_at; // c'est une date mais souci de deserialisation

}
