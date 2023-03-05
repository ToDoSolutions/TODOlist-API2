package com.todolist.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

// TODO: Revisar todas estas excepciones.
@RestControllerAdvice
public class CustomErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException exception, ServletWebRequest webRequest) {
        String message = new ArrayList<>(exception.getConstraintViolations()).get(0).getMessage();
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), message, webRequest.getRequest().getRequestURI(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException exception, ServletWebRequest webRequest) {
        String message = exception.getMessage().indexOf(":") > 0 ? exception.getMessage().split(":")[1] : exception.getMessage();
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), message, webRequest.getRequest().getRequestURI(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException exception, ServletWebRequest webRequest) {
        String message = exception.getMessage().indexOf(":") > 0 ? exception.getMessage().split(":")[1] : exception.getMessage();
        System.out.println(Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString).reduce("", (a, b) -> a + "" + b));
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), message, webRequest.getRequest().getRequestURI(), HttpStatus.NOT_FOUND.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, ServletWebRequest webRequest) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getBindingResult().getFieldError() != null ? Objects.requireNonNull(exception.getBindingResult().getFieldError().getDefaultMessage()).replace(":", "->") : "No info.", webRequest.getRequest().getRequestURI(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ExceptionResponse> handleNumberFormatException(NumberFormatException exception, ServletWebRequest webRequest) {
        String message = exception.getMessage().indexOf(":") > 0 ? exception.getMessage().split(":")[1] : exception.getMessage();
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), "Error while parsing the next string " + message + ".", webRequest.getRequest().getRequestURI(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ExceptionResponse> handleDateTimeParseException(DateTimeParseException exception, ServletWebRequest webRequest) {
        String message = exception.getMessage().indexOf(":") > 0 ? exception.getMessage().split(":")[1].split("'")[1] : exception.getMessage().split("'")[1];
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), "The date " + message + " is not valid and it should be in the format yyyy-MM-dd.", webRequest.getRequest().getRequestURI(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
