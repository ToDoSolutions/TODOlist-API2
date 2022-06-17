package com.todolist.utilities;

import java.util.Date;

public class ExceptionResponse {
    private final Date timestamp;
    private final String msg;
    private final String path;
    private final String status;

    public ExceptionResponse(Date timestamp, String message, String path, String status) {
        super();
        this.timestamp = timestamp;
        this.msg = message != null ? (message.contains(":") ? message.split(":")[1] : message) : message;
        this.path = path;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMsg() {
        return msg;
    }

    public String getPath() {
        return path;
    }

}
