package com.todolist.utilities;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.ConstraintViolationException;
import java.util.Date;

@RestControllerAdvice
public class CustomErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException exception,
                                                                                ServletWebRequest webRequest) {

        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), exception.getMessage(),
                webRequest.getDescription(false), HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ExceptionResponse> handleNullPointerException(NullPointerException exception) {

        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), exception.getMessage().split("\\|")[0], exception.getMessage().split("\\|")[1], HttpStatus.NOT_FOUND.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), exception.getMessage().split("\\|")[0], exception.getMessage().split("\\|")[1], HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
