package com.swivel.ignite.registration.controller;

import com.swivel.ignite.registration.dto.request.StudentCreateRequestDto;
import com.swivel.ignite.registration.dto.response.StudentResponseDto;
import com.swivel.ignite.registration.entity.Student;
import com.swivel.ignite.registration.entity.Tuition;
import com.swivel.ignite.registration.enums.ErrorResponseStatusType;
import com.swivel.ignite.registration.enums.SuccessResponseStatusType;
import com.swivel.ignite.registration.exception.*;
import com.swivel.ignite.registration.service.StudentService;
import com.swivel.ignite.registration.service.TuitionService;
import com.swivel.ignite.registration.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Student Controller
 */
@RestController
@RequestMapping("api/v1/student")
@Slf4j
public class StudentController extends Controller {

    private final StudentService studentService;
    private final TuitionService tuitionService;

    @Autowired
    public StudentController(StudentService studentService, TuitionService tuitionService) {
        this.studentService = studentService;
        this.tuitionService = tuitionService;
    }

    /**
     * This method creates a new student
     *
     * @param requestDto student create request dto
     * @return success(student response)/ error response
     */
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> createStudent(@RequestBody StudentCreateRequestDto requestDto) {
        try {
            if (!requestDto.isRequiredAvailable()) {
                log.error("Required fields missing in tuition create request DTO for creating student");
                return getBadRequestResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            Student student = new Student(requestDto);
            studentService.createStudent(student);
            StudentResponseDto responseDto = new StudentResponseDto(student);
            log.debug("Created student {}", responseDto.toLogJson());
            return getSuccessResponse(SuccessResponseStatusType.CREATE_STUDENT, responseDto);
        } catch (StudentAlreadyExistsException e) {
            log.error("Student already exists for create student with requestDto: {}", requestDto.toLogJson(), e);
            return getBadRequestResponse(ErrorResponseStatusType.STUDENT_ALREADY_EXISTS);
        } catch (RegistrationServiceException e) {
            log.error("Creating tuition was failed for requestDto: {}", requestDto.toLogJson(), e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method is to log in a student
     *
     * @param requestDto student create request dto
     * @return success(student response)/ error response
     */
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> loginStudent(@RequestBody StudentCreateRequestDto requestDto) {
        try {
            if (!requestDto.isRequiredAvailable()) {
                log.error("Required fields missing in tuition create request DTO for creating student");
                return getBadRequestResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            Student student = studentService.login(requestDto);
            StudentResponseDto responseDto = new StudentResponseDto(student);
            log.debug("Logged in the student {}", responseDto.toLogJson());
            return getSuccessResponse(SuccessResponseStatusType.LOGIN_STUDENT, responseDto);
        } catch (StudentNotFoundException e) {
            log.error("Student not found for login student with requestDto: {}", requestDto.toLogJson(), e);
            return getBadRequestResponse(ErrorResponseStatusType.STUDENT_NOT_FOUND);
        } catch (UsernamePasswordNotMatchException e) {
            log.error("Username and password don't match for login student request dto: {}", requestDto.toLogJson(), e);
            return getBadRequestResponse(ErrorResponseStatusType.USERNAME_PASSWORD_NOT_MATCH);
        } catch (RegistrationServiceException e) {
            log.error("Creating tuition was failed for requestDto: {}", requestDto.toLogJson(), e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method is used to get a student by id
     *
     * @param studentId student id
     * @return success(student)/ error response
     */
    @GetMapping(path = "/get/{studentId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> getStudentById(@PathVariable(name = "studentId") String studentId) {
        try {
            Student student = studentService.findById(studentId);
            StudentResponseDto responseDto = new StudentResponseDto(student);
            log.debug("Retrieved student of id: {}", studentId);
            return getSuccessResponse(SuccessResponseStatusType.GET_STUDENT, responseDto);
        } catch (StudentNotFoundException e) {
            log.error("Student not found for studentId: {}", studentId, e);
            return getBadRequestResponse(ErrorResponseStatusType.STUDENT_NOT_FOUND);
        } catch (RegistrationServiceException e) {
            log.error("Getting student was failed for studentId: {}", studentId, e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method deletes a student by id
     *
     * @param studentId student id
     * @return success/ error response
     */
    @DeleteMapping(path = "/delete/{studentId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> deleteStudent(@PathVariable(name = "studentId") String studentId) {
        try {
            Student student = studentService.findById(studentId);
            studentService.deleteStudent(student);
            log.debug("Successfully deleted the student of id: {}", studentId);
            return getSuccessResponse(SuccessResponseStatusType.DELETE_STUDENT, null);
        } catch (StudentNotFoundException e) {
            log.error("Student not found for studentId: {}", studentId, e);
            return getBadRequestResponse(ErrorResponseStatusType.STUDENT_NOT_FOUND);
        } catch (RegistrationServiceException e) {
            log.error("Deleting student was failed for studentId: {}", studentId, e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method adds a student to the tuition
     *
     * @param studentId student id
     * @param tuitionId tuition id
     * @return success/ error response
     */
    @PostMapping(path = "/add/{studentId}/{tuitionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> addStudentToTuition(@PathVariable(name = "studentId") String studentId,
                                                               @PathVariable(name = "tuitionId") String tuitionId) {
        try {
            Student student = studentService.findById(studentId);
            if (student.getTuition() != null) {
                log.error("Student already enrolled in a tuition");
                return getBadRequestResponse(ErrorResponseStatusType.STUDENT_ALREADY_ENROLLED_IN_A_TUITION);
            }
            Tuition tuition = tuitionService.findById(tuitionId);
            Student student1 = studentService.addStudentToTuition(student, tuition);
            StudentResponseDto responseDto = new StudentResponseDto(student1);
            log.debug("Successfully added student of id: {} to the tuition", studentId);
            return getSuccessResponse(SuccessResponseStatusType.ADD_TUITION_STUDENT, responseDto);
        } catch (TuitionNotFoundException e) {
            log.error("Tuition not found for add student to tuition of id: {}", tuitionId, e);
            return getBadRequestResponse(ErrorResponseStatusType.TUITION_NOT_FOUND);
        } catch (StudentNotFoundException e) {
            log.error("Student not found for add student of id: {} to tuition", studentId, e);
            return getBadRequestResponse(ErrorResponseStatusType.STUDENT_NOT_FOUND);
        } catch (RegistrationServiceException e) {
            log.error("Failed to add student to tuition for student id: {}", studentId, e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method removes a student from the tuition
     *
     * @param studentId student id
     * @param tuitionId tuition id
     * @return success/ error response
     */
    @PostMapping(path = "/remove/{studentId}/{tuitionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> removeStudentFromTuition(@PathVariable(name = "studentId") String studentId,
                                                                    @PathVariable(name = "tuitionId") String tuitionId) {
        try {
            Student student = studentService.findById(studentId);
            Tuition tuition = tuitionService.findById(tuitionId);
            if (student.getTuition() == null || student.getTuition() != tuition) {
                log.error("Student is not enrolled in tuition id: " + tuition.getId());
                return getBadRequestResponse(ErrorResponseStatusType.STUDENT_NOT_ENROLLED_IN_TUITION);
            }
            studentService.removeStudentFromTuition(student);
            log.debug("Successfully removed student of id: {} from the tuition", studentId);
            return getSuccessResponse(SuccessResponseStatusType.REMOVE_TUITION_STUDENT, null);
        } catch (TuitionNotFoundException e) {
            log.error("Tuition not found for removing student from tuition of id: {}", tuitionId, e);
            return getBadRequestResponse(ErrorResponseStatusType.TUITION_NOT_FOUND);
        } catch (StudentNotFoundException e) {
            log.error("Student not found for removing student of id: {}", studentId, e);
            return getBadRequestResponse(ErrorResponseStatusType.STUDENT_NOT_FOUND);
        } catch (RegistrationServiceException e) {
            log.error("Failed to remove student from tuition for student id: {}", studentId, e);
            return getInternalServerErrorResponse();
        }
    }
}
