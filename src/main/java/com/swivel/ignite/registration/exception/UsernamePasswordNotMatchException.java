package com.swivel.ignite.registration.exception;

/**
 * UsernamePasswordNotMatchException
 */
public class UsernamePasswordNotMatchException extends RuntimeException {

    /**
     * UsernamePasswordNotMatchException with error message.
     *
     * @param errorMessage error message
     */
    public UsernamePasswordNotMatchException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * UsernamePasswordNotMatchException with error message and throwable error
     *
     * @param errorMessage error message
     * @param error        error
     */
    public UsernamePasswordNotMatchException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
