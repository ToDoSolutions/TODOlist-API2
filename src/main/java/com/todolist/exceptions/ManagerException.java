package com.todolist.exceptions;

import com.google.common.base.Splitter;
import lombok.Getter;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class ManagerException {

    public static final String MSG = "msg";
    public static final String PATH = "path";
    public static final String STATUS = "status";
    public static final String TIMESTAMP = "timestamp";
    public static final String LEFT_KEY = "{";
    public static final String RIGHT_KEY = "}";
    public static final String EMPTY = "";
    public static final String SLASH = "\"";
    public static final String COMMA = ",";
    public static final String TWO_POINTS = ":";
    public static final String NO_INFORMATION = "No information";
    public static final String ERROR = "error";
    private LocalDate timestamp;
    private String msg;
    private String path;
    private String status;


    private ManagerException(HttpStatusCodeException exception) {
        Map<String, String> info;
        try {
            info = new HashMap<>(Splitter.on(COMMA)
                    .withKeyValueSeparator(TWO_POINTS)
                    .split(exception.getResponseBodyAsString().replace(LEFT_KEY, EMPTY).replace(RIGHT_KEY, EMPTY).replace(SLASH, EMPTY)));
            this.timestamp = LocalDate.parse(info.get(TIMESTAMP));
            this.msg = info.get(MSG).trim();
            this.path = info.get(PATH).trim();
            this.status = info.get(STATUS).trim();
        } catch (Exception e) {
            info = new HashMap<>(Splitter.on(COMMA)
                    .withKeyValueSeparator(TWO_POINTS)
                    .split(
                            exception.getResponseBodyAsString()
                                    .replace(LEFT_KEY, EMPTY)
                                    .replace(RIGHT_KEY, EMPTY)
                                    .replace(SLASH, EMPTY)
                                    .substring(40)
                    ));
            this.timestamp = LocalDate.now();
            this.msg = NO_INFORMATION;
            this.path = info.get(PATH);
            this.status = info.get(ERROR);
        }
    }

    public static ManagerException of(HttpStatusCodeException exception) {
        return new ManagerException(exception);
    }

    public ManagerException assertStatus(String expected) {
        if (!Objects.equals(status, expected.trim()))
            throw new AssertionError("Error Status: " + status + " != " + expected);
        return this;
    }

    public ManagerException assertPath(String expected) {
        if (!Objects.equals(path, expected.trim()))
            throw new AssertionError("Error Path: " + path + " != " + expected);
        return this;
    }

    public ManagerException assertMsg(String expected) {
        if (!Objects.equals(this.msg, expected.trim()))
            throw new AssertionError("Error Message: " + this.msg + " != " + expected);
        return this;
    }
}
