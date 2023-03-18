package com.todolist.utilities;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;


public class WriterManager {

    private String input;

    public WriterManager(String template) throws IOException {
        String location = getResource(template).orElseThrow(() -> new FileNotFoundException(template)).getFile();
        File file = new File(location);
        input = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    private Optional<URL> getResource(String template) {
        return Optional.ofNullable(getClass().getClassLoader().getResource(template));
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
