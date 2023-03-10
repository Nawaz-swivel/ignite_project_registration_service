package com.swivel.ignite.registration.controller;

import com.swivel.ignite.registration.dto.request.TuitionCreateRequestDto;
import com.swivel.ignite.registration.dto.response.TuitionListResponseDto;
import com.swivel.ignite.registration.dto.response.TuitionResponseDto;
import com.swivel.ignite.registration.entity.Tuition;
import com.swivel.ignite.registration.enums.ErrorResponseStatusType;
import com.swivel.ignite.registration.enums.SuccessResponseStatusType;
import com.swivel.ignite.registration.exception.RegistrationServiceException;
import com.swivel.ignite.registration.exception.TuitionAlreadyExistsException;
import com.swivel.ignite.registration.exception.TuitionNotFoundException;
import com.swivel.ignite.registration.service.TuitionService;
import com.swivel.ignite.registration.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Tuition Controller
 */
@RestController
@RequestMapping("api/v1/tuition")
@Slf4j
public class TuitionController extends Controller {

    private final TuitionService tuitionService;

    @Autowired
    public TuitionController(TuitionService tuitionService) {
        this.tuitionService = tuitionService;
    }

    /**
     * This method creates a new tuition class
     *
     * @param requestDto tuition create request dto
     * @return success(tuition response)/ error response
     */
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> createTuition(@RequestBody TuitionCreateRequestDto requestDto) {
        try {
            if (!requestDto.isRequiredAvailable()) {
                log.error("Required fields missing in tuition create request DTO for creating tuition");
                return getBadRequestResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            Tuition tuition = new Tuition(requestDto);
            tuitionService.createTuition(tuition);
            TuitionResponseDto responseDto = new TuitionResponseDto(tuition);
            log.debug("Created tuition {}", responseDto.toLogJson());
            return getSuccessResponse(SuccessResponseStatusType.CREATE_TUITION, responseDto);
        } catch (TuitionAlreadyExistsException e) {
            log.error("Tuition already exists for create tuition with requestDto: {}", requestDto.toLogJson(), e);
            return getBadRequestResponse(ErrorResponseStatusType.TUITION_ALREADY_EXISTS);
        } catch (RegistrationServiceException e) {
            log.error("Creating tuition was failed for requestDto: {}", requestDto.toLogJson(), e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method returns a tuition class by id
     *
     * @param id tuition class id
     * @return success(tuition response)/ error response
     */
    @GetMapping(path = "/get/{tuitionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> getTuitionById(@PathVariable(name = "tuitionId") String id) {
        try {
            Tuition tuition = tuitionService.findById(id);
            TuitionResponseDto responseDto = new TuitionResponseDto(tuition);
            log.debug("Successfully returned the tuition {}", responseDto.toLogJson());
            return getSuccessResponse(SuccessResponseStatusType.READ_TUITION, responseDto);
        } catch (TuitionNotFoundException e) {
            log.error("Tuition not found for getting tuition by id: {}", id, e);
            return getBadRequestResponse(ErrorResponseStatusType.TUITION_NOT_FOUND);
        } catch (RegistrationServiceException e) {
            log.error("Failed to get tuition from DB for id: {}", id, e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method deletes a tuition class by id
     *
     * @param tuitionId tuitionId
     * @return success/ error response
     */
    @DeleteMapping(path = "/delete/{tuitionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> deleteTuition(@PathVariable(name = "tuitionId") String tuitionId) {
        try {
            Tuition tuition = tuitionService.findById(tuitionId);
            tuitionService.deleteTuition(tuition);
            log.debug("Deleted tuition of id: {}", tuitionId);
            return getSuccessResponse(SuccessResponseStatusType.DELETE_TUITION, null);
        } catch (TuitionNotFoundException e) {
            log.error("Tuition not found for tuitionId: {}", tuitionId, e);
            return getBadRequestResponse(ErrorResponseStatusType.TUITION_NOT_FOUND);
        } catch (RegistrationServiceException e) {
            log.error("Deleting tuition was failed for tuitionId: {}", tuitionId, e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method is used to get all tuition
     *
     * @return success(tuition list)/ error response
     */
    @GetMapping(path = "/get/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> getAllTuition() {
        try {
            List<Tuition> tuitionList = tuitionService.getAll();
            TuitionListResponseDto responseDto = new TuitionListResponseDto(tuitionList);
            log.debug("Returned all tuition");
            return getSuccessResponse(SuccessResponseStatusType.RETURNED_ALL_TUITION, responseDto);
        } catch (RegistrationServiceException e) {
            log.error("Failed to get all tuition", e);
            return getInternalServerErrorResponse();
        }
    }
}
