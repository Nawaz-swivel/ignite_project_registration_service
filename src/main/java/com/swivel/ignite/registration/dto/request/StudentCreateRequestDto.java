package com.swivel.ignite.registration.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for Student creation request
 */
@Getter
@Setter
public class StudentCreateRequestDto extends RequestDto {

    private String name;
    private String password;

    @Override
    public String toLogJson() {
        return toJson();
    }

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(name) && isNonEmpty(password);
    }
}
