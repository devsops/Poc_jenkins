package com.bosch.pai.retail.analytics.exception;

import com.bosch.pai.retail.common.responses.StatusMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestControllerAdvice
@EnableWebMvc
public class ServiceExceptionHandler {
    private final Logger logger = LoggerFactory
            .getLogger(ServiceExceptionHandler.class);

    @ExceptionHandler(AnalyticsServiceException.class)
    public ResponseEntity<StatusMessage> handle(AnalyticsServiceException e) {
        logger.debug("Handling exception .");
        return new ResponseEntity<>(e.getStatusMessage(),
                HttpStatus.EXPECTATION_FAILED);
    }
}
