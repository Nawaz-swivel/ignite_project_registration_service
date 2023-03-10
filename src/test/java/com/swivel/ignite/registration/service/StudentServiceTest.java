package com.swivel.ignite.registration.service;

import com.swivel.ignite.registration.entity.Student;
import com.swivel.ignite.registration.entity.Tuition;
import com.swivel.ignite.registration.exception.RegistrationServiceException;
import com.swivel.ignite.registration.exception.StudentAlreadyExistsException;
import com.swivel.ignite.registration.exception.StudentNotFoundException;
import com.swivel.ignite.registration.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * This class tests {@link StudentService} class
 */
class StudentServiceTest {

    private static final String STUDENT_ID = "sid-123456789";
    private static final String TUITION_ID = "tid-123456789";
    private static final String ERROR = "ERROR";
    @Mock
    private PaymentService paymentService;
    @Mock
    private StudentRepository studentRepository;
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        studentService = new StudentService(studentRepository, paymentService);
    }

    /**
     * Start of tests for createStudent method
     */
    @Test
    void Should_CreateStudent_When_CreatingStudentIsSuccessful() {
        when(studentRepository.findById(anyString())).thenReturn(Optional.empty());
        studentService.createStudent(getSampleStudent());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void Should_ThrowStudentAlreadyExistsException_When_CreatingStudentForStudentAlreadyExists() {
        Student student = getSampleStudent();

        when(studentRepository.findById(anyString())).thenReturn(Optional.of(getSampleStudent()));
        StudentAlreadyExistsException exception = assertThrows(StudentAlreadyExistsException.class, () ->
                studentService.createStudent(student));
        assertEquals("Student already exists in DB", exception.getMessage());
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_CreatingStudentForFailedToCheckForStudentInDB() {
        Student student = getSampleStudent();

        when(studentRepository.findById(anyString())).thenThrow(new DataAccessException(ERROR) {
        });
        RegistrationServiceException exception = assertThrows(RegistrationServiceException.class, () ->
                studentService.createStudent(student));
        assertEquals("Failed to check for student existence in DB for id: " + getSampleStudent().getId(),
                exception.getMessage());
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_CreatingStudentForFailedToSaveStudentInDB() {
        Student student = getSampleStudent();

        when(studentRepository.findById(anyString())).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenThrow(new DataAccessException(ERROR) {
        });
        RegistrationServiceException exception = assertThrows(RegistrationServiceException.class, () ->
                studentService.createStudent(student));
        assertEquals("Failed to save student to DB for student id: " + getSampleStudent().getId(),
                exception.getMessage());
    }

    /**
     * Start of tests for findById method
     */
    @Test
    void Should_ReturnStudent_When_FindingStudentByIdIsSuccessful() {
        when(studentRepository.findById(anyString())).thenReturn(Optional.of(getSampleStudent()));
        assertEquals(STUDENT_ID, studentService.findById(STUDENT_ID).getId());
    }

    @Test
    void Should_ThrowStudentNotFoundException_When_FindingStudentByIdForStudentNotFound() {
        when(studentRepository.findById(anyString())).thenReturn(Optional.empty());
        StudentNotFoundException exception = assertThrows(StudentNotFoundException.class, () ->
                studentService.findById(STUDENT_ID));
        assertEquals("Student not found for student id: " + STUDENT_ID, exception.getMessage());
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_FindingStudentByIdForFailedToFindStudentById() {
        when(studentRepository.findById(anyString())).thenThrow(new DataAccessException(ERROR) {
        });
        RegistrationServiceException exception = assertThrows(RegistrationServiceException.class, () ->
                studentService.findById(STUDENT_ID));
        assertEquals("Failed to find student by id for student id: " + STUDENT_ID, exception.getMessage());
    }

    /**
     * Start of tests for deleteStudent method
     */
    @Test
    void Should_DeleteStudent_When_DeletingStudentIsSuccessful() throws IOException {
        doNothing().when(paymentService).deleteByStudentId(STUDENT_ID);
        studentService.deleteStudent(getSampleStudent());
        verify(studentRepository).delete(any(Student.class));
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_DeletingStudentForFailedToDeleteStudent() throws IOException {
        doNothing().when(paymentService).deleteByStudentId(STUDENT_ID);
        doThrow(new DataAccessException(ERROR) {
        }).when(studentRepository).delete(any(Student.class));
        RegistrationServiceException exception = assertThrows(RegistrationServiceException.class, () ->
                studentService.deleteStudent(getSampleStudent()));
        assertEquals("Failed to delete student of id: " + STUDENT_ID, exception.getMessage());
    }

    /**
     * Start of tests for addStudentToTuition method
     */
    @Test
    void Should_AddStudentToTuition_When_AddingStudentToTuitionIsSuccessful() {
        studentService.addStudentToTuition(getSampleStudent(), getSampleTuition());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_AddingStudentToTuitionIsFailed() {
        Student student = getSampleStudent();
        Tuition tuition = getSampleTuition();

        doThrow(new DataAccessException(ERROR) {
        }).when(studentRepository).save(any(Student.class));
        RegistrationServiceException exception = assertThrows(RegistrationServiceException.class, () ->
                studentService.addStudentToTuition(student, tuition));
        assertEquals("Failed to add student of id: " + STUDENT_ID + " to tuition", exception.getMessage());
    }

    /**
     * Start of tests for removeStudentFromTuition method
     */
    @Test
    void Should_RemoveStudentFromTuition_When_RemovingStudentFromTuitionIsSuccessful() {
        studentService.removeStudentFromTuition(getSampleStudent());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_RemovingStudentFromTuitionIsFailed() {
        Student student = getSampleStudent();

        doThrow(new DataAccessException(ERROR) {
        }).when(studentRepository).save(any(Student.class));
        RegistrationServiceException exception = assertThrows(RegistrationServiceException.class, () ->
                studentService.removeStudentFromTuition(student));
        assertEquals("Failed to remove student of id: " + STUDENT_ID + " from tuition", exception.getMessage());
    }

    /**
     * This method returns a sample student
     *
     * @return Student
     */
    private Student getSampleStudent() {
        Student student = new Student();
        student.setId(STUDENT_ID);
        return student;
    }

    /**
     * This method returns a sample Tuition
     *
     * @return Tuition
     */
    private Tuition getSampleTuition() {
        Tuition tuition = new Tuition();
        tuition.setId(TUITION_ID);
        return tuition;
    }
}
