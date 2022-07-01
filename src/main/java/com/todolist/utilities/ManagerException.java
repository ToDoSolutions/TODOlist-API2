package com.todolist.utilities;

import com.google.common.base.Splitter;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ManagerException {

    private LocalDate timestamp;
    private String msg;
    private String path;
    private String status;


    public ManagerException(HttpClientErrorException exception) {
        Map<String, String> info;
        try {
            info = new HashMap<>(Splitter.on(",")
                    .withKeyValueSeparator(":")
                    .split(exception.getResponseBodyAsString().replace("{", "").replace("}", "").replace("\"", "")));
            this.timestamp = LocalDate.parse(info.get("timestamp"));
            this.msg = info.get("msg");
            this.path = info.get("path");
            this.status = info.get("status");
        } catch (Exception e) {
            System.out.println(exception.getResponseBodyAsString());
            info = new HashMap<>(Splitter.on(",")
                    .withKeyValueSeparator(":")
                    .split(
                            exception.getResponseBodyAsString()
                                    .replace("{", "")
                                    .replace("}", "")
                                    .replace("\"", "")
                                    .substring(40)
                    ));
            this.timestamp = LocalDate.now();
            this.msg = "No information";
            this.path = info.get("path");
            this.status = info.get("error");
        }


    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public String getMsg() {
        return msg;
    }

    public String getPath() {
        return path;
    }

    public String getStatus() {
        return status;
    }
}
