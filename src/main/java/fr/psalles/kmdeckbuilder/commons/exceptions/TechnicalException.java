package fr.psalles.kmdeckbuilder.commons.exceptions;

public class TechnicalException extends  RuntimeException {
    private final String message;

    public TechnicalException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}

