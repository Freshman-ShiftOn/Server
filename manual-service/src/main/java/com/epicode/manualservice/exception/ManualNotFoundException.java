package com.epicode.manualservice.exception;

public class ManualNotFoundException extends RuntimeException {
    public ManualNotFoundException(String message) {
        super(message);
    }
}