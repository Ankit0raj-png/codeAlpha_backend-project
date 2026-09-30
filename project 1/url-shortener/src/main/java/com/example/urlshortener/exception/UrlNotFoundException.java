package com.example.urlshortener.exception;

/**
 * Thrown when someone requests a short code that doesn't exist (or has expired).
 * We'll catch this in the controller and turn it into a proper 404 HTTP response.
 */
public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String message) {
        super(message);
    }
}
