package com.todolist.config.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;

@RestControllerAdvice
public class CustomErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException exception,
                                                                                ServletWebRequest webRequest) {
        if (!exception.getMessage().contains("ConstraintViolationImpl")) {
            ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getMessage(),
                    webRequest.getDescription(false).replace("uri=", ""), HttpStatus.BAD_REQUEST.getReasonPhrase());
            return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        } else {
            ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getMessage().split("'")[1],
                    webRequest.getDescription(false).replace("uri=", ""), HttpStatus.BAD_REQUEST.getReasonPhrase());
            return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        }

    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ExceptionResponse> handleNullPointerException(NullPointerException exception) {
        // System.out.println("NullPointerException");
        try {
            ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getMessage().split("\\|")[0], exception.getMessage().split("\\|")[1], HttpStatus.NOT_FOUND.getReasonPhrase());
            return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getMessage(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase());
            return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        // System.out.println("IllegalArgumentException");
        try {
            ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getMessage().split("\\|")[0], exception.getMessage().split("\\|")[1], HttpStatus.BAD_REQUEST.getReasonPhrase());
            return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getMessage(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase());
            return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        // System.out.println("MethodArgumentNotValidException");
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getBindingResult().getFieldError() != null ? exception.getBindingResult().getFieldError().getDefaultMessage() : "No info.", "When creating or updating an entity.", HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(RuntimeException exception) {
        // System.out.println("RuntimeException");
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getMessage(), exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
