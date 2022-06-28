package com.todolist.utilities;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
        try {
            ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), exception.getMessage().split("\\|")[0], exception.getMessage().split("\\|")[1], HttpStatus.NOT_FOUND.getReasonPhrase());
            return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), exception.getMessage(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase());
            return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        try {
            ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), exception.getMessage().split("\\|")[0], exception.getMessage().split("\\|")[1], HttpStatus.BAD_REQUEST.getReasonPhrase());
            return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new IllegalArgumentException(exception.getMessage());
            //ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), exception.getMessage(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase());
            //return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), exception.getBindingResult().getFieldError() != null ? exception.getBindingResult().getFieldError().getDefaultMessage() : "No info.", "When creating or updating an entity.", HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }


}
