package com.swivel.ignite.registration.dto.response;

import com.swivel.ignite.registration.entity.Tuition;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Tuition list DTO for response
 */
@Getter
public class TuitionListResponseDto extends ResponseDto {

    private final List<TuitionResponseDto> tuitionList = new ArrayList<>();

    public TuitionListResponseDto(List<Tuition> tuitionList) {
        for (Tuition t : tuitionList) {
            this.tuitionList.add(new TuitionResponseDto(t));
        }
    }
}
