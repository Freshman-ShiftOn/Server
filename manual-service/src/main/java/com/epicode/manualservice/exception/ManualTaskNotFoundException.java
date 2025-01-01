package com.epicode.manualservice.exception;

public class ManualTaskNotFoundException extends RuntimeException {
    public ManualTaskNotFoundException(String message) {
        super(message);
    }
}
