package com.swivel.ignite.registration.service;

import com.swivel.ignite.registration.entity.Student;
import com.swivel.ignite.registration.entity.Tuition;
import com.swivel.ignite.registration.exception.RegistrationServiceException;
import com.swivel.ignite.registration.exception.TuitionAlreadyExistsException;
import com.swivel.ignite.registration.exception.TuitionNotFoundException;
import com.swivel.ignite.registration.repository.TuitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Tuition Service
 */
@Service
public class TuitionService {

    private final StudentService studentService;
    private final TuitionRepository tuitionRepository;
    private final PaymentService paymentService;

    @Autowired
    public TuitionService(TuitionRepository tuitionRepository, StudentService studentService,
                          PaymentService paymentService) {
        this.studentService = studentService;
        this.tuitionRepository = tuitionRepository;
        this.paymentService = paymentService;
    }

    /**
     * This method creates a Tuition in the database
     *
     * @param tuition tuition
     */
    public void createTuition(Tuition tuition) {
        try {
            if (isTuitionExists(tuition.getName()))
                throw new TuitionAlreadyExistsException("Tuition already exists in DB");
            tuitionRepository.save(tuition);
        } catch (DataAccessException e) {
            throw new RegistrationServiceException("Failed to save tuition to DB for tuition id: {}" + tuition.getId(), e);
        }
    }

    /**
     * This method finds a tuition by id
     *
     * @param id tuition id
     * @return Tuition/ null
     */
    public Tuition findById(String id) {
        try {
            Optional<Tuition> optionalTuition = tuitionRepository.findById(id);
            if (!optionalTuition.isPresent())
                throw new TuitionNotFoundException("Tuition not found for id: " + id);
            return optionalTuition.get();
        } catch (DataAccessException e) {
            throw new RegistrationServiceException("Failed to get tuition from DB for tuition id: " + id, e);
        }
    }

    /**
     * This method deletes a tuition
     *
     * @param tuition tuition
     */
    @Transactional
    public void deleteTuition(Tuition tuition) {
        try {
            Set<Student> students = tuition.getStudents();
            for (Student s : students) {
                studentService.removeStudentFromTuition(s);
            }
            paymentService.deleteByTuitionId(tuition.getId());
            tuitionRepository.delete(tuition);
        } catch (DataAccessException | IOException e) {
            throw new RegistrationServiceException("Failed to delete tuition of id: " + tuition.getId(), e);
        }
    }

    /**
     * This method checks if tuition already exists in the DB
     *
     * @param name tuition name
     * @return true/false
     */
    private boolean isTuitionExists(String name) {
        try {
            return tuitionRepository.findByName(name).isPresent();
        } catch (DataAccessException e) {
            throw new RegistrationServiceException("Failed to check for tuition existence in DB for name: " + name, e);
        }
    }

    /**
     * This method returns all tuition
     *
     * @return list of tuition
     */
    public List<Tuition> getAll() {
        try {
            return tuitionRepository.findAll();
        } catch (DataAccessException e) {
            throw new RegistrationServiceException("Failed to to get all tuition", e);
        }
    }
}
