package com.todolist.config.errors;

import com.google.common.base.Splitter;
import lombok.Getter;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
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
}
