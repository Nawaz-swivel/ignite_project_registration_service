package com.swivel.ignite.registration.exception;

/**
 * RegistrationServiceException
 */
public class RegistrationServiceException extends RuntimeException {

    /**
     * RegistrationServiceException with error message.
     *
     * @param errorMessage error message
     */
    public RegistrationServiceException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * RegistrationServiceException with error message and throwable error
     *
     * @param errorMessage error message
     * @param error        error
     */
    public RegistrationServiceException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
