package fr.psalles.kmdeckbuilder.commons.exceptions;

public class UnauthorizedException extends RuntimeException {

    private final String message;

    public UnauthorizedException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
