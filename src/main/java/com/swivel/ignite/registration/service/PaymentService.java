package com.swivel.ignite.registration.service;

import com.swivel.ignite.registration.exception.PaymentServiceHttpClientErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Payment Microservice
 */
@Slf4j
@Service
public class PaymentService {


    private static final String FAILED_TO_DELETE_PAYMENT_BY_TUITION_ID = "Failed to delete payments by tuitionId";
    private static final String FAILED_TO_DELETE_PAYMENT_BY_STUDENT_ID = "Failed to delete payments by studentId";
    private final RestTemplate restTemplate;
    private final String getDeleteByTuitionIdUrl;
    private final String getDeleteByStudentIdUrl;

    public PaymentService(@Value("${payment.baseUrl}") String baseUrl,
                          @Value("${payment.deleteByTuitionIdUrl}") String deleteByTuitionIdUrl,
                          @Value("${payment.deleteByStudentIdUrl}") String deleteByStudentIdUrl,
                          RestTemplate restTemplate) {
        this.getDeleteByTuitionIdUrl = baseUrl + deleteByTuitionIdUrl;
        this.getDeleteByStudentIdUrl = baseUrl + deleteByStudentIdUrl;
        this.restTemplate = restTemplate;
    }

    /**
     * This method deletes a payment by tuition id in payment microservice
     *
     * @param tuitionId tuition id
     * @throws IOException
     */
    public void deleteByTuitionId(String tuitionId) throws IOException {
        Map<String, String> uriParam = new HashMap<>();
        uriParam.put("tuitionId", tuitionId);
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(getDeleteByTuitionIdUrl).build();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            log.debug("Calling payment service to delete all payments by tuitionId. url: {}", getDeleteByTuitionIdUrl);
            ResponseEntity<String> result = restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class, uriParam);
            log.debug("Deleting all payments by tuition id was successful. statusCode: {}", result.getStatusCode());
        } catch (HttpClientErrorException e) {
            throw new PaymentServiceHttpClientErrorException(e.getStatusCode(), FAILED_TO_DELETE_PAYMENT_BY_TUITION_ID,
                    e.getResponseBodyAsString(), e);
        }
    }

    /**
     * This method deletes a payment by student id in payment microservice
     *
     * @param studentId student id
     * @throws IOException
     */
    public void deleteByStudentId(String studentId) throws IOException {
        Map<String, String> uriParam = new HashMap<>();
        uriParam.put("studentId", studentId);
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(getDeleteByStudentIdUrl).build();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            log.debug("Calling payment service to delete all payments by studentId. url: {}", getDeleteByStudentIdUrl);
            ResponseEntity<String> result = restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, entity,
                    String.class, uriParam);
            log.debug("Deleting all payments by student id was successful. statusCode: {}", result.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.error(" Error from reg: {}", e.getMessage());
            throw new PaymentServiceHttpClientErrorException(e.getStatusCode(), FAILED_TO_DELETE_PAYMENT_BY_STUDENT_ID,
                    e.getResponseBodyAsString(), e);
        }
    }
}
