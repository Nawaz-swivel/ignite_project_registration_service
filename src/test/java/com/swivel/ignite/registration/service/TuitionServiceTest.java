package com.swivel.ignite.registration.service;

import com.swivel.ignite.registration.entity.Student;
import com.swivel.ignite.registration.entity.Tuition;
import com.swivel.ignite.registration.exception.RegistrationServiceException;
import com.swivel.ignite.registration.exception.TuitionAlreadyExistsException;
import com.swivel.ignite.registration.exception.TuitionNotFoundException;
import com.swivel.ignite.registration.repository.TuitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * This class tests {@link TuitionService} class
 */
class TuitionServiceTest {

    private static final String TUITION_ID = "tid-123456789";
    private static final String STUDENT_ID = "sid-123456789";
    private static final String TUITION_NAME = "Perera Tuition";
    private static final String ERROR = "ERROR";
    private TuitionService tuitionService;
    @Mock
    private StudentService studentService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private TuitionRepository tuitionRepository;

    @BeforeEach
    void setUp() {
        initMocks(this);
        tuitionService = new TuitionService(tuitionRepository, studentService, paymentService);
    }

    /**
     * Start of test for createTuition method
     */
    @Test
    void Should_CreateTuition_When_CreatingTuitionIsSuccessful() {
        when(tuitionRepository.findByName(anyString())).thenReturn(Optional.empty());
        tuitionService.createTuition(getSampleTuition());
        verify(tuitionRepository).save(any(Tuition.class));
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_CreatingTuitionForFailedToCheckForTuitionInDB() {
        Tuition tuition = getSampleTuition();

        when(tuitionRepository.findByName(anyString())).thenThrow(new DataAccessException(ERROR) {
        });
        RegistrationServiceException exception = assertThrows(RegistrationServiceException.class, () ->
                tuitionService.createTuition(tuition));
        assertEquals("Failed to check for tuition existence in DB for name: " + TUITION_NAME, exception.getMessage());
    }

    @Test
    void Should_ThrowTuitionAlreadyExistsException_When_CreatingTuitionForTuitionAlreadyExists() {
        Tuition tuition = getSampleTuition();

        when(tuitionRepository.findByName(anyString())).thenReturn(Optional.of(getSampleTuition()));
        TuitionAlreadyExistsException exception = assertThrows(TuitionAlreadyExistsException.class, () ->
                tuitionService.createTuition(tuition));
        assertEquals("Tuition already exists in DB", exception.getMessage());
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_CreatingTuitionIsFailed() {
        Tuition tuition = getSampleTuition();

        when(tuitionRepository.findByName(anyString())).thenReturn(Optional.empty());
        doThrow(new DataAccessException(ERROR) {
        }).when(tuitionRepository).save(any(Tuition.class));
        RegistrationServiceException exception = assertThrows(RegistrationServiceException.class, () ->
                tuitionService.createTuition(tuition));
        assertEquals("Failed to save tuition to DB for tuition id: {}" + TUITION_ID, exception.getMessage());
    }

    /**
     * Start of test for findById method
     */
    @Test
    void Should_ReturnTuition_When_FindingTuitionByIdIsSuccessful() {
        when(tuitionRepository.findById(anyString())).thenReturn(Optional.of(getSampleTuition()));
        assertEquals(TUITION_ID, tuitionService.findById(TUITION_ID).getId());
    }

    @Test
    void Should_ThrowTuitionNotFoundException_When_FindingTuitionByIdForTuitionNotFound() {
        when(tuitionRepository.findById(anyString())).thenReturn(Optional.empty());
        TuitionNotFoundException exception = assertThrows(TuitionNotFoundException.class, () ->
                tuitionService.findById(TUITION_ID));
        assertEquals("Tuition not found for id: " + TUITION_ID, exception.getMessage());
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_FindingTuitionByIdIsFailed() {
        when(tuitionRepository.findById(anyString())).thenThrow(new DataAccessException(ERROR) {
        });
        RegistrationServiceException exception = assertThrows(RegistrationServiceException.class, () ->
                tuitionService.findById(TUITION_ID));
        assertEquals("Failed to get tuition from DB for tuition id: " + TUITION_ID, exception.getMessage());
    }

    /**
     * Start of test for deleteTuition method
     */
    @Test
    void Should_DeleteTuition_When_DeletingTuitionIsSuccessful() throws IOException {
        doNothing().when(studentService).removeStudentFromTuition(any(Student.class));
        doNothing().when(paymentService).deleteByTuitionId(anyString());
        tuitionService.deleteTuition(getSampleTuition());
        verify(tuitionRepository).delete(any(Tuition.class));
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_DeletingTuitionIsFailed() throws IOException {
        Tuition tuition = getSampleTuition();

        doNothing().when(studentService).removeStudentFromTuition(any(Student.class));
        doNothing().when(paymentService).deleteByTuitionId(anyString());
        doThrow(new DataAccessException(ERROR) {
        }).when(tuitionRepository).delete(any(Tuition.class));
        RegistrationServiceException exception = assertThrows(RegistrationServiceException.class, () ->
                tuitionService.deleteTuition(tuition));
        assertEquals("Failed to delete tuition of id: " + TUITION_ID, exception.getMessage());
    }

    /**
     * Start of test for getAll method
     */
    @Test
    void Should_ReturnAllTuition_When_GettingAllIsSuccessful() {
        when(tuitionRepository.findAll()).thenReturn(getSampleTuitionList());
        assertEquals(TUITION_ID, tuitionService.getAll().get(0).getId());
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_GettingAllIsFailed() {
        when(tuitionRepository.findAll()).thenThrow(new DataAccessException(ERROR) {
        });
        RegistrationServiceException exception = assertThrows(RegistrationServiceException.class, () ->
                tuitionService.getAll());
        assertEquals("Failed to to get all tuition", exception.getMessage());
    }

    /**
     * This method returns a sample Tuition
     *
     * @return Tuition
     */
    private Tuition getSampleTuition() {
        Tuition tuition = new Tuition();
        tuition.setId(TUITION_ID);
        tuition.setName(TUITION_NAME);
        tuition.setStudents(getSampleStudentSet());
        return tuition;
    }

    /**
     * This method returns a sample Student
     *
     * @return Student
     */
    private Student getSampleStudent() {
        Student student = new Student();
        student.setId(STUDENT_ID);
        return student;
    }

    /**
     * This method returns a sample Tuition list
     *
     * @return Tuition List
     */
    private List<Tuition> getSampleTuitionList() {
        List<Tuition> tuitionList = new ArrayList<>();
        tuitionList.add(getSampleTuition());
        return tuitionList;
    }

    /**
     * This method returns a sample Student Set
     *
     * @return Student Set
     */
    private Set<Student> getSampleStudentSet() {
        Set<Student> studentSet = new HashSet<>();
        studentSet.add(getSampleStudent());
        return studentSet;
    }
}
