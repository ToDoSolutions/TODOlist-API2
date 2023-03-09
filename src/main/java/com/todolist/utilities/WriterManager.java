package com.todolist.utilities;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.util.Optional;
import java.util.function.Function;


public class WriterManager {

    private String input;

    public WriterManager(String template) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(template);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Optional<String> text = reader.lines().reduce((s, s2) -> s + "\n" + s2);
        text.ifPresent(s -> this.input = s);
        reader.close();
    }

    public WriterManager map(Function<String, String> function) {
        this.input = function.apply(input);
        return this;
    }

    public ResponseEntity<String> get(String title) {
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + title + "\"")
                .body(input);
    }


}
