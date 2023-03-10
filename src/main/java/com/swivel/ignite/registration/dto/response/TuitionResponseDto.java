package com.swivel.ignite.registration.dto.response;

import com.swivel.ignite.registration.entity.Student;
import com.swivel.ignite.registration.entity.Tuition;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Tuition DTO for response
 */
@Getter
public class TuitionResponseDto extends ResponseDto {

    private final String tuitionId;
    private final String name;
    private final String location;
    private final Set<String> studentIds = new HashSet<>();

    public TuitionResponseDto(Tuition tuition) {
        this.tuitionId = tuition.getId();
        this.name = tuition.getName();
        this.location = tuition.getLocation();
        if (tuition.getStudents() != null)
            for (Student s : tuition.getStudents()) {
                this.studentIds.add(s.getId());
            }
    }
}
