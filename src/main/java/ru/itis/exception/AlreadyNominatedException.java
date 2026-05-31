package ru.itis.exception;

public class AlreadyNominatedException extends RuntimeException {
    public AlreadyNominatedException(String message) {
        super(message);
    }
}