package com.swivel.ignite.registration.repository;

import com.swivel.ignite.registration.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Student Repository
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    /**
     * This method returns a student by name
     *
     * @param name name
     * @return Student/null
     */
    Optional<Student> findByName(String name);
}
