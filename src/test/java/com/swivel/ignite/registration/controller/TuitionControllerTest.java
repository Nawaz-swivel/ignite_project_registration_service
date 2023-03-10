package com.swivel.ignite.registration.controller;

import com.swivel.ignite.registration.dto.request.TuitionCreateRequestDto;
import com.swivel.ignite.registration.entity.Tuition;
import com.swivel.ignite.registration.enums.ErrorResponseStatusType;
import com.swivel.ignite.registration.enums.SuccessResponseStatusType;
import com.swivel.ignite.registration.exception.RegistrationServiceException;
import com.swivel.ignite.registration.exception.TuitionAlreadyExistsException;
import com.swivel.ignite.registration.exception.TuitionNotFoundException;
import com.swivel.ignite.registration.service.TuitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class tests {@link TuitionController} class
 */
class TuitionControllerTest {

    private static final String TUITION_ID = "tid-123456789";
    private static final String TUITION_NAME = "Perera Tuition";
    private static final String TUITION_LOCATION = "Nittambuwa";
    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String ERROR_STATUS = "ERROR";
    private static final String SUCCESS_MESSAGE = "Successfully returned the data.";
    private static final String ERROR_MESSAGE = "Oops!! Something went wrong. Please try again.";
    private static final String ERROR = "ERROR";
    private static final String CREATE_TUITION_URI = "/api/v1/tuition/create";
    private static final String GET_TUITION_BY_ID_URI = "/api/v1/tuition/get/{tuitionId}";
    private static final String DELETE_TUITION_ID_URI = "/api/v1/tuition/delete/{tuitionId}";
    private static final String GET_ALL_TUITION_URI = "/api/v1/tuition/get/all";
    private MockMvc mockMvc;
    @Mock
    private TuitionService tuitionService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        TuitionController tuitionController = new TuitionController(tuitionService);
        mockMvc = MockMvcBuilders.standaloneSetup(tuitionController).build();
    }

    /**
     * Start of tests for create tuition
     * Api context: /api/v1/tuition/create
     */
    @Test
    void Should_ReturnOk_When_CreatingTuitionIsSuccessful() throws Exception {
        doNothing().when(tuitionService).createTuition(any(Tuition.class));

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_TUITION_URI)
                        .content(getSampleTuitionCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.CREATE_TUITION.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.CREATE_TUITION.getCode()))
                .andExpect(jsonPath("$.data.name").value(TUITION_NAME))
                .andExpect(jsonPath("$.data.location").value(TUITION_LOCATION))
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingTuitionForMissingRequiredFields() throws Exception {
        TuitionCreateRequestDto dto = getSampleTuitionCreateRequestDto();
        dto.setLocation("");

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_TUITION_URI)
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
    void Should_ReturnBadRequest_When_CreatingTuitionForTuitionAlreadyExists() throws Exception {
        doThrow(new TuitionAlreadyExistsException(ERROR)).when(tuitionService).createTuition(any(Tuition.class));

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_TUITION_URI)
                        .content(getSampleTuitionCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.TUITION_ALREADY_EXISTS
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.TUITION_ALREADY_EXISTS
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_CreatingTuitionIsFailed() throws Exception {
        doThrow(new RegistrationServiceException(ERROR)).when(tuitionService).createTuition(any(Tuition.class));

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_TUITION_URI)
                        .content(getSampleTuitionCreateRequestDto().toJson())
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
     * Start of tests for get tuition by id
     * Api context: /api/v1/tuition/get/{tuitionId}
     */
    @Test
    void Should_ReturnOk_When_GettingTuitionByIdIsSuccessful() throws Exception {
        when(tuitionService.findById(anyString())).thenReturn(getSampleTuition());

        String uri = GET_TUITION_BY_ID_URI.replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.READ_TUITION.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.READ_TUITION.getCode()))
                .andExpect(jsonPath("$.data.tuitionId").value(TUITION_ID))
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_GettingTuitionByIdForTuitionNotFound() throws Exception {
        when(tuitionService.findById(anyString())).thenThrow(new TuitionNotFoundException(ERROR));

        String uri = GET_TUITION_BY_ID_URI.replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.TUITION_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.TUITION_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_GettingTuitionByIdIsFailed() throws Exception {
        when(tuitionService.findById(anyString())).thenThrow(new RegistrationServiceException(ERROR));

        String uri = GET_TUITION_BY_ID_URI.replace("{tuitionId}", TUITION_ID);
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
     * Start of tests for delete tuition
     * Api context: /api/v1/tuition/delete/{tuitionId}
     */
    @Test
    void Should_ReturnOk_When_DeletingTuitionIsSuccessful() throws Exception {
        when(tuitionService.findById(anyString())).thenReturn(getSampleTuition());
        doNothing().when(tuitionService).deleteTuition(any(Tuition.class));

        String uri = DELETE_TUITION_ID_URI.replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.DELETE_TUITION.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.DELETE_TUITION.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_DeletingTuitionForTuitionNotFound() throws Exception {
        when(tuitionService.findById(anyString())).thenThrow(new TuitionNotFoundException(ERROR));

        String uri = DELETE_TUITION_ID_URI.replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.TUITION_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.TUITION_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_DeletingTuitionIsFailed() throws Exception {
        when(tuitionService.findById(anyString())).thenThrow(new RegistrationServiceException(ERROR));

        String uri = DELETE_TUITION_ID_URI.replace("{tuitionId}", TUITION_ID);
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
     * Start of tests for get all tuition
     * Api context: /api/v1/tuition/get/all
     */
    @Test
    void Should_ReturnOk_When_GettingAllTuitionIsSuccessful() throws Exception {
        when(tuitionService.getAll()).thenReturn(getSampleTuitionList());

        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_TUITION_URI)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.RETURNED_ALL_TUITION
                        .getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.RETURNED_ALL_TUITION
                        .getCode()))
                .andExpect(jsonPath("$.data.tuitionList[0].tuitionId").value(TUITION_ID))
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_GettingAllTuitionIsFailed() throws Exception {
        when(tuitionService.getAll()).thenThrow(new RegistrationServiceException(ERROR));

        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_TUITION_URI)
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
     * This method returns a sample TuitionCreateRequestDto
     *
     * @return TuitionCreateRequestDto
     */
    private TuitionCreateRequestDto getSampleTuitionCreateRequestDto() {
        TuitionCreateRequestDto requestDto = new TuitionCreateRequestDto();
        requestDto.setName(TUITION_NAME);
        requestDto.setLocation(TUITION_LOCATION);
        return requestDto;
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

    /**
     * This method returns a sample tuition list
     *
     * @return Tuition List
     */
    private List<Tuition> getSampleTuitionList() {
        List<Tuition> tuitionList = new ArrayList<>();
        tuitionList.add(getSampleTuition());
        return tuitionList;
    }
}
