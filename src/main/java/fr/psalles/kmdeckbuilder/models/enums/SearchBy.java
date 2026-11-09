package fr.psalles.kmdeckbuilder.models.enums;

public enum SearchBy {
    FAVORITE("favoriteCount"),
    RECENT("creationDate"),
    AP_COST("costAP"),
    DUST_COST("costDust");

    private final String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    SearchBy(String fieldName) {
        this.fieldName = fieldName;
    }
}