package com.swivel.ignite.registration.dto.response;

import com.swivel.ignite.registration.entity.Student;
import lombok.Getter;

import java.util.Date;

/**
 * Student DTO for response
 */
@Getter
public class StudentResponseDto extends ResponseDto {

    private final String studentId;
    private final String name;
    private final String tuitionId;
    private final Date tuitionJoinedOn;

    public StudentResponseDto(Student student) {
        this.studentId = student.getId();
        this.name = student.getName();
        this.tuitionId = student.getTuition() != null ? student.getTuition().getId() : null;
        this.tuitionJoinedOn = student.getTuitionJoinedOn();
    }
}
