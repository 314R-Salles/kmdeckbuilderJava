package fr.psalles.kmdeckbuilder.models.extern.auth0;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class PaginatedResponse {
    protected int total;
    protected int start;
    protected int limit;
    protected int length;
}
