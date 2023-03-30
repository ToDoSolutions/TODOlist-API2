package com.todolist.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;

@RestControllerAdvice
public class CustomErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException exception, ServletWebRequest webRequest) {
        String message = new ArrayList<>(exception.getConstraintViolations()).get(0).getMessage();
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), message, webRequest.getRequest().getRequestURI(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleForbiddenException(ForbiddenException exception, ServletWebRequest webRequest) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getMessage(), webRequest.getRequest().getRequestURI(), HttpStatus.FORBIDDEN.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RequestTimeoutException.class)
    public ResponseEntity<ExceptionResponse> handleRequestTimeoutException(RequestTimeoutException exception, ServletWebRequest webRequest) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getMessage(), webRequest.getRequest().getRequestURI(), HttpStatus.REQUEST_TIMEOUT.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException exception, ServletWebRequest webRequest) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getMessage(), webRequest.getRequest().getRequestURI(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException exception, ServletWebRequest webRequest) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getMessage(), webRequest.getRequest().getRequestURI(), HttpStatus.NOT_FOUND.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ExceptionResponse> handleServerException(ServerException exception, ServletWebRequest webRequest) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getMessage(), webRequest.getRequest().getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
