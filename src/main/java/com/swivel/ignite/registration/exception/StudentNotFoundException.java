package com.swivel.ignite.registration.exception;

/**
 * Student Not Found Exception
 */
public class StudentNotFoundException extends RuntimeException {

    /**
     * Student Not Found Exception with error message.
     *
     * @param errorMessage error message
     */
    public StudentNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Student Not Found Exception with error message and throwable error
     *
     * @param errorMessage error message
     * @param error        error
     */
    public StudentNotFoundException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
