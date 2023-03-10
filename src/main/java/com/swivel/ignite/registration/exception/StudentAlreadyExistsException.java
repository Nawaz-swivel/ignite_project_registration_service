package com.swivel.ignite.registration.exception;

/**
 * Student Already Exists Exception
 */
public class StudentAlreadyExistsException extends RuntimeException {

    /**
     * Student Already Exists Exception with error message.
     *
     * @param errorMessage error message
     */
    public StudentAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Student Already Exists Exception with error message and throwable error
     *
     * @param errorMessage error message
     * @param error        error
     */
    public StudentAlreadyExistsException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
