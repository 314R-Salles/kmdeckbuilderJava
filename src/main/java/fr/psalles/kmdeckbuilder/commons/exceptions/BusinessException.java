package fr.psalles.kmdeckbuilder.commons.exceptions;

public class BusinessException extends  RuntimeException {
    private final String message;

    public BusinessException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}

