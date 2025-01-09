package com.epicode.exception;

public class NoBranchFoundException extends RuntimeException {
    public NoBranchFoundException(String message) {
        super(message);
    }
}