package com.swivel.ignite.registration.wrapper;

import com.swivel.ignite.registration.dto.BaseDto;
import com.swivel.ignite.registration.enums.ResponseStatusType;
import lombok.Getter;

/**
 * Response wrapper
 */
@Getter
public class ResponseWrapper implements BaseDto {
    private final ResponseStatusType status;
    private final String message;
    private final String displayMessage;

    /**
     * @param status         status
     * @param message        dev message
     *                       //     * @param data           data
     * @param displayMessage display message
     */
    public ResponseWrapper(ResponseStatusType status, String message, String displayMessage) {
        this.status = status;
        this.message = message;
        this.displayMessage = displayMessage;
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
