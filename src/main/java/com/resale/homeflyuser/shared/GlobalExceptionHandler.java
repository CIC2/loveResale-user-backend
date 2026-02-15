package com.resale.homeflyuser.shared;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.resale.homeflyuser.utils.ReturnObject;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ReturnObject<?>> handleInvalidEnum(HttpMessageNotReadableException ex) {

        if (ex.getCause() instanceof InvalidFormatException ife &&
                ife.getTargetType().isEnum()) {

            String message = "Invalid value for field. Must be one of: ";
            Object[] enumConstants = ife.getTargetType().getEnumConstants();
            if (enumConstants != null) {
                message += String.join(", ",
                        java.util.Arrays.stream(enumConstants)
                                .map(Object::toString)
                                .toList());
            }

            ReturnObject<?> response = new ReturnObject<>(message, false, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        throw ex;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ReturnObject<?>> handleGeneralError(Exception ex) {
        log.error("Unexpected error", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ReturnObject<>("An unexpected error occurred. Please try again later.", false, null));
    }

    @ExceptionHandler({
            SQLIntegrityConstraintViolationException.class,
            DataIntegrityViolationException.class,
            TransactionSystemException.class,
            JpaSystemException.class
    })
    public ResponseEntity<ReturnObject<?>> handleDatabaseConstraint(Exception ex) {
        log.error("Database constraint or integrity violation", ex);

        // Unwrap the real cause
        Throwable rootCause = ex.getCause();
        while (rootCause != null && rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }

        String rootMessage = rootCause != null && rootCause.getMessage() != null
                ? rootCause.getMessage().toLowerCase()
                : "";

        String message = "A database constraint was violated. Please check your data and try again.";

        if (rootMessage.contains("data too long")) {
            message = "One or more fields exceed the allowed length. Please review your input.";
        } else if (rootMessage.contains("cannot be null") || rootMessage.contains("null value")) {
            message = "Some required fields are missing. Please make sure all mandatory fields are provided.";
        } else if (rootMessage.contains("duplicate") || rootMessage.contains("unique constraint")) {
            message = "A record with the same value already exists. Please use a unique value.";
        } else if (rootMessage.contains("foreign key")) {
            message = "Invalid reference. Please make sure related data (like project or customer) exists.";
        } else if (rootMessage.contains("constraint") && rootMessage.contains("project")) {
            message = "Project information is missing or invalid. Please include a valid project ID.";
        }

        ReturnObject<?> response = new ReturnObject<>(message, false, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ReturnObject<?>> handleEntityNotFound(EntityNotFoundException ex) {
            ReturnObject<?> response = new ReturnObject<>(ex.getMessage(), false, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

}

