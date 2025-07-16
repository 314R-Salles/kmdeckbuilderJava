package fr.psalles.kmdeckbuilder.models.enums;

public enum SearchBy {
    FAVORITE("favoriteCount"), RECENT("creationDate");

    private final String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    SearchBy(String fieldName) {
        this.fieldName = fieldName;
    }
}