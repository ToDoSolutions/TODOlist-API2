package com.todolist.config.errors;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ExceptionResponse {
    private final LocalDate timestamp;
    private final String msg;
    private final String path;
    private final String status;

    public ExceptionResponse(LocalDate timestamp, String message, String path, String status) {
        super();
        this.timestamp = timestamp;
        this.msg = message != null ? (message.contains(":") ? message.split(":")[1] : message) : message;
        this.path = path;
        this.status = status;
    }

}
