package com.swivel.ignite.registration.service;

import com.swivel.ignite.registration.dto.request.StudentCreateRequestDto;
import com.swivel.ignite.registration.entity.Student;
import com.swivel.ignite.registration.entity.Tuition;
import com.swivel.ignite.registration.exception.RegistrationServiceException;
import com.swivel.ignite.registration.exception.StudentAlreadyExistsException;
import com.swivel.ignite.registration.exception.StudentNotFoundException;
import com.swivel.ignite.registration.exception.UsernamePasswordNotMatchException;
import com.swivel.ignite.registration.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * Student Service
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PaymentService paymentService;

    @Autowired
    public StudentService(StudentRepository studentRepository, PaymentService paymentService) {
        this.studentRepository = studentRepository;
        this.paymentService = paymentService;
    }

    /**
     * This method creates a Student in the database
     *
     * @param student student
     */
    public void createStudent(Student student) {
        try {
            if (isStudentExists(student.getId()))
                throw new StudentAlreadyExistsException("Student already exists in DB");
            studentRepository.save(student);
        } catch (DataAccessException e) {
            throw new RegistrationServiceException("Failed to save student to DB for student id: " + student.getId(), e);
        }
    }

    /**
     * This method login a student
     *
     * @param requestDto StudentCreateRequestDto
     * @return Student
     */
    public Student login(StudentCreateRequestDto requestDto) {
        try {
            Optional<Student> optionalStudent = studentRepository.findByName(requestDto.getName());
            if (!optionalStudent.isPresent())
                throw new StudentNotFoundException("Student not found in DB for name: " + requestDto.getName());
            Student student = optionalStudent.get();
            if (!Objects.equals(student.getPassword(), requestDto.getPassword()))
                throw new UsernamePasswordNotMatchException("Username password not match for student");
            return student;
        } catch (DataAccessException e) {
            throw new RegistrationServiceException("Failed to login the student", e);
        }
    }

    /**
     * This method returns a student by id
     *
     * @param studentId student id
     * @return Student/null
     */
    public Student findById(String studentId) {
        try {
            Optional<Student> optionalStudent = studentRepository.findById(studentId);
            if (!optionalStudent.isPresent())
                throw new StudentNotFoundException("Student not found for student id: " + studentId);
            return optionalStudent.get();
        } catch (DataAccessException e) {
            throw new RegistrationServiceException("Failed to find student by id for student id: " + studentId, e);
        }
    }

    /**
     * This method deletes a student
     *
     * @param student student
     */
    @Transactional
    public void deleteStudent(Student student) {
        try {
            paymentService.deleteByStudentId(student.getId());
            studentRepository.delete(student);
        } catch (DataAccessException | IOException e) {
            throw new RegistrationServiceException("Failed to delete student of id: " + student.getId(), e);
        }
    }

    /**
     * This method add a student to a tuition
     *
     * @param student student
     * @param tuition tuition
     * @return Student
     */
    public Student addStudentToTuition(Student student, Tuition tuition) {
        try {
            student.setTuition(tuition);
            student.setTuitionJoinedOn(new Date());
            return studentRepository.save(student);
        } catch (DataAccessException e) {
            throw new RegistrationServiceException("Failed to add student of id: " + student.getId() + " to tuition", e);
        }
    }

    /**
     * This method removes a student from tuition
     *
     * @param student student
     */
    public void removeStudentFromTuition(Student student) {
        try {
            student.setTuition(null);
            student.setTuitionJoinedOn(null);
            studentRepository.save(student);
        } catch (DataAccessException e) {
            throw new RegistrationServiceException("Failed to remove student of id: " + student.getId() + " from tuition", e);
        }
    }

    /**
     * This method checks if student already exists in the DB
     *
     * @param id student id
     * @return true/false
     */
    private boolean isStudentExists(String id) {
        try {
            return studentRepository.findById(id).isPresent();
        } catch (DataAccessException e) {
            throw new RegistrationServiceException("Failed to check for student existence in DB for id: " + id, e);
        }
    }
}
