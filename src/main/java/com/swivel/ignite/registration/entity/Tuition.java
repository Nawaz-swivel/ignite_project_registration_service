package com.swivel.ignite.registration.entity;

import com.swivel.ignite.registration.dto.request.TuitionCreateRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * Tuition entity
 */
@Entity
@Table(name = "tuition")
@NoArgsConstructor
@Getter
@Setter
public class Tuition implements Serializable {

    @Transient
    private static final String TUITION_ID_PREFIX = "tid-";

    @Id
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String location;
    @OneToMany(mappedBy = "tuition")
    private Set<Student> students;

    public Tuition(TuitionCreateRequestDto requestDto) {
        this.id = TUITION_ID_PREFIX + UUID.randomUUID();
        this.name = requestDto.getName();
        this.location = requestDto.getLocation();
    }
}
