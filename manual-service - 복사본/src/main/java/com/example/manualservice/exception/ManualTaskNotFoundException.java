package com.example.manualservice.exception;

public class ManualTaskNotFoundException extends RuntimeException {
    public ManualTaskNotFoundException(String message) {
        super(message);
    }
}
