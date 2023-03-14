package com.swivel.ignite.registration.controller;

import com.swivel.ignite.registration.dto.request.StudentCreateRequestDto;
import com.swivel.ignite.registration.entity.Student;
import com.swivel.ignite.registration.entity.Tuition;
import com.swivel.ignite.registration.enums.ErrorResponseStatusType;
import com.swivel.ignite.registration.enums.SuccessResponseStatusType;
import com.swivel.ignite.registration.exception.RegistrationServiceException;
import com.swivel.ignite.registration.exception.StudentAlreadyExistsException;
import com.swivel.ignite.registration.exception.StudentNotFoundException;
import com.swivel.ignite.registration.exception.TuitionNotFoundException;
import com.swivel.ignite.registration.service.StudentService;
import com.swivel.ignite.registration.service.TuitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class tests {@link StudentController} class
 */
class StudentControllerTest {


    private static final String STUDENT_ID = "sid-123456789";
    private static final String TUITION_ID = "tid-123456789";
    private static final String STUDENT_NAME = "Mohamed Nawaz";
    private static final String STUDENT_PASSWORD = "123456789";
    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String ERROR_STATUS = "ERROR";
    private static final String SUCCESS_MESSAGE = "Successfully returned the data.";
    private static final String ERROR_MESSAGE = "Oops!! Something went wrong. Please try again.";
    private static final String ERROR = "ERROR";
    private static final String CREATE_STUDENT_URI = "/api/v1/student/create";
    private static final String GET_STUDENT_BY_ID_URI = "/api/v1/student/get/{studentId}";
    private static final String DELETE_STUDENT_URI = "/api/v1/student/delete/{studentId}";
    private static final String ADD_STUDENT_TO_TUITION_URI = "/api/v1/student/add/{studentId}/{tuitionId}";
    private static final String REMOVE_STUDENT_FROM_TUITION_URI = "/api/v1/student/remove/{studentId}/{tuitionId}";
    private MockMvc mockMvc;
    @Mock
    private StudentService studentService;
    @Mock
    private TuitionService tuitionService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        StudentController studentController = new StudentController(studentService, tuitionService);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }

    /**
     * Start of tests for create student
     * Api context: /api/v1/student/create
     */
    @Test
    void Should_ReturnOk_When_CreatingStudentIsSuccessful() throws Exception {
        doNothing().when(studentService).createStudent(any(Student.class));

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_STUDENT_URI)
                        .content(getSampleStudentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.CREATE_STUDENT.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.CREATE_STUDENT.getCode()))
                .andExpect(jsonPath("$.data.name").value(STUDENT_NAME))
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingStudentForMissingRequiredFields() throws Exception {
        StudentCreateRequestDto dto = getSampleStudentCreateRequestDto();
        dto.setName("");

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_STUDENT_URI)
                        .content(dto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingStudentForStudentAlreadyExists() throws Exception {
        doThrow(new StudentAlreadyExistsException(ERROR)).when(studentService).createStudent(any(Student.class));

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_STUDENT_URI)
                        .content(getSampleStudentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.STUDENT_ALREADY_EXISTS
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.STUDENT_ALREADY_EXISTS
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_CreatingStudentIsFailed() throws Exception {
        doThrow(new RegistrationServiceException(ERROR)).when(studentService).createStudent(any(Student.class));

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_STUDENT_URI)
                        .content(getSampleStudentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * Start of tests for get student by id
     * Api context: /api/v1/student/get/{studentId}
     */
    @Test
    void Should_ReturnOk_When_GettingStudentByIdIsSuccessful() throws Exception {
        when(studentService.findById(anyString())).thenReturn(getSampleStudent());

        String uri = GET_STUDENT_BY_ID_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.GET_STUDENT.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.GET_STUDENT.getCode()))
                .andExpect(jsonPath("$.data.studentId").value(STUDENT_ID))
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_GettingStudentByIdForStudentNotFound() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new StudentNotFoundException(ERROR));

        String uri = GET_STUDENT_BY_ID_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_GettingStudentByIdIsFailed() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new RegistrationServiceException(ERROR));

        String uri = GET_STUDENT_BY_ID_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * Start of tests for delete student
     * Api context: /api/v1/student/delete/{studentId}
     */
    @Test
    void Should_ReturnOk_When_DeletingStudentIsSuccessful() throws Exception {
        when(studentService.findById(anyString())).thenReturn(getSampleStudent());
        doNothing().when(studentService).deleteStudent(any(Student.class));

        String uri = DELETE_STUDENT_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.DELETE_STUDENT.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.DELETE_STUDENT.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_DeletingStudentForStudentNotFound() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new StudentNotFoundException(ERROR));

        String uri = DELETE_STUDENT_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_DeletingStudentIsFailed() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new RegistrationServiceException(ERROR));

        String uri = DELETE_STUDENT_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * Start of tests for add student to tuition
     * Api context: /api/v1/student/add/{studentId}/{tuitionId}
     */
    @Test
    void Should_ReturnOk_When_AddingStudentToTuitionIsSuccessful() throws Exception {
        when(studentService.findById(anyString())).thenReturn(getSampleStudent());
        when(tuitionService.findById(anyString())).thenReturn(getSampleTuition());
        when(studentService.addStudentToTuition(any(Student.class), any(Tuition.class))).thenReturn(getSampleStudent());

        String uri = ADD_STUDENT_TO_TUITION_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.ADD_TUITION_STUDENT
                        .getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.ADD_TUITION_STUDENT
                        .getCode()))
                .andExpect(jsonPath("$.data.studentId").value(STUDENT_ID))
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_AddingStudentToTuitionForStudentAlreadyEnrolledInATuition() throws Exception {
        Student student = getSampleStudent();
        student.setTuition(getSampleTuition());

        when(studentService.findById(anyString())).thenReturn(student);

        String uri = ADD_STUDENT_TO_TUITION_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType
                        .STUDENT_ALREADY_ENROLLED_IN_A_TUITION.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType
                        .STUDENT_ALREADY_ENROLLED_IN_A_TUITION.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_AddingStudentToTuitionForTuitionNotFound() throws Exception {
        when(studentService.findById(anyString())).thenReturn(getSampleStudent());
        when(tuitionService.findById(anyString())).thenThrow(new TuitionNotFoundException(ERROR));

        String uri = ADD_STUDENT_TO_TUITION_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.TUITION_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.TUITION_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_AddingStudentToTuitionForStudentNotFound() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new StudentNotFoundException(ERROR));

        String uri = ADD_STUDENT_TO_TUITION_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_AddingStudentToTuitionIsFailed() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new RegistrationServiceException(ERROR));

        String uri = ADD_STUDENT_TO_TUITION_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * Start of tests for remove student from tuition
     * Api context: /api/v1/student/remove/{studentId}/{tuitionId}
     */
    @Test
    void Should_ReturnOk_When_RemovingStudentFromTuitionIsSuccessful() throws Exception {
        Student student = getSampleStudent();
        student.setTuition(getSampleTuition());

        when(studentService.findById(anyString())).thenReturn(student);
        when(tuitionService.findById(anyString())).thenReturn(student.getTuition());
        when(studentService.removeStudentFromTuition(any(Student.class))).thenReturn(getSampleStudent());

        String uri = REMOVE_STUDENT_FROM_TUITION_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.REMOVE_TUITION_STUDENT
                        .getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.REMOVE_TUITION_STUDENT
                        .getCode()))
                .andExpect(jsonPath("$.data.studentId").value(STUDENT_ID))
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_RemovingStudentFromTuitionForStudentNotEnrolledInTuiton() throws Exception {
        when(studentService.findById(anyString())).thenReturn(getSampleStudent());
        when(tuitionService.findById(anyString())).thenReturn(getSampleTuition());

        String uri = REMOVE_STUDENT_FROM_TUITION_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.STUDENT_NOT_ENROLLED_IN_TUITION
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.STUDENT_NOT_ENROLLED_IN_TUITION
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_RemovingStudentFromTuitionForTuitionNotFound() throws Exception {
        when(studentService.findById(anyString())).thenReturn(getSampleStudent());
        when(tuitionService.findById(anyString())).thenThrow(new TuitionNotFoundException(ERROR));

        String uri = REMOVE_STUDENT_FROM_TUITION_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.TUITION_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.TUITION_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_RemovingStudentFromTuitionForStudentNotFound() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new StudentNotFoundException(ERROR));

        String uri = REMOVE_STUDENT_FROM_TUITION_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_RemovingStudentFromTuitionIsFailed() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new RegistrationServiceException(ERROR));

        String uri = REMOVE_STUDENT_FROM_TUITION_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * This method returns a sample StudentCreateRequestDto
     *
     * @return StudentCreateRequestDto
     */
    private StudentCreateRequestDto getSampleStudentCreateRequestDto() {
        StudentCreateRequestDto requestDto = new StudentCreateRequestDto();
        requestDto.setName(STUDENT_NAME);
        requestDto.setPassword(STUDENT_PASSWORD);
        return requestDto;
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
     * This method returns a sample tuition
     *
     * @return Tuition
     */
    private Tuition getSampleTuition() {
        Tuition tuition = new Tuition();
        tuition.setId(TUITION_ID);
        return tuition;
    }
}
