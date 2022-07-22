package com.todolist.exceptions;

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
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException exception, ServletWebRequest webRequest) {
        String message = exception.getMessage().indexOf(":") > 0 ? exception.getMessage().split(":")[1] : exception.getMessage();
        System.out.println("ConstraintViolationException: " + message);
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), message, webRequest.getRequest().getRequestURI(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException exception, ServletWebRequest webRequest) {
        String message = exception.getMessage().indexOf(":") > 0 ? exception.getMessage().split(":")[1] : exception.getMessage();
        System.out.println("BadRequestException: " + message);
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), message, webRequest.getRequest().getRequestURI(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException exception, ServletWebRequest webRequest) {
        String message = exception.getMessage().indexOf(":") > 0 ? exception.getMessage().split(":")[1] : exception.getMessage();
        System.out.println("NotFoundException: " + message);
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), message, webRequest.getRequest().getRequestURI(), HttpStatus.NOT_FOUND.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, ServletWebRequest webRequest) {
        System.out.println("MethodArgumentNotValidException");
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getBindingResult().getFieldError() != null ? exception.getBindingResult().getFieldError().getDefaultMessage().replace(":", "->") : "No info.", webRequest.getRequest().getRequestURI(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    /*
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ExceptionResponse> handleNumberFormatException(NumberFormatException exception, ServletWebRequest webRequest) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), exception.getMessage().replace(":", "->"), webRequest.getRequest().getRequestURI(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ExceptionResponse> handleNullPointerException(NullPointerException exception, ServletWebRequest webRequest) {
        System.out.println("NullPointerException");
        String message = exception.getMessage().indexOf(":") > 0 ? exception.getMessage().split(":")[1] : exception.getMessage();
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), message, webRequest.getRequest().getRequestURI(), HttpStatus.NOT_FOUND.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException exception, ServletWebRequest webRequest) {
        System.out.println("IllegalArgumentException");
        System.out.println("IllegalArgumentException: " + exception.getMessage());
        String message = exception.getMessage().indexOf(":") > 0 ? exception.getMessage().split(":")[1] : exception.getMessage();
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDate.now(), message, webRequest.getRequest().getRequestURI(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
     */




}
