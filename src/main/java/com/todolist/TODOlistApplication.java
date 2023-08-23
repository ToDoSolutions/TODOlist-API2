package com.todolist;

import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class TODOlistApplication {
    public static final String MODE = "mode";
    public static final String PROFILES = "spring.profiles.active";
    public static final String LOCAL = "local";
    public static final String CLOUD = "cloud";
    public static final String APPLICATION_PROPERTIES = "application.properties";
    public static final String APPLICATION_LOCAL_PROPERTIES = "application-local.properties";
    public static final String DATASOURCE_URL = "spring.datasource.url";
    public static final String DB = "db";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String DATASOURCE_PASSWORD = "spring.datasource.password";
    public static final String DATASOURCE_USERNAME = "spring.datasource.username";


    public static void main(String[] args) throws IOException {
        if (args.length > 0)
            prepareEnvironment(args);
        SpringApplication.run(TODOlistApplication.class, args);
    }

    private static void prepareEnvironment(String[] args) throws IOException {
        Map<String, String> commands = Stream.of(args).collect(Collectors.toMap(s -> s.split("=")[0].replaceAll("-", ""), s -> s.split("=")[1]));
        Properties properties = new Properties();
        properties.load(TODOlistApplication.class.getClassLoader().getResourceAsStream(APPLICATION_PROPERTIES));
        if (commands.containsKey(MODE)) {
            if (commands.get(MODE).equals(LOCAL)) {
                properties.setProperty(PROFILES, LOCAL);
                localEnvironment(commands);
            } else if (commands.get(MODE).equals(CLOUD)) {
                properties.setProperty(PROFILES, CLOUD);
            }
        }
        // Se debe de modificar el mismo archivo properties
        try (OutputStream output = new FileOutputStream(APPLICATION_PROPERTIES)) {
            properties.store(output, null);
        }
    }

    private static void localEnvironment(Map<String, String> commands) throws IOException {
        Properties localProperties = new Properties();
        localProperties.load(TODOlistApplication.class.getClassLoader().getResourceAsStream(APPLICATION_LOCAL_PROPERTIES));
        if (commands.containsKey(DB))
            localProperties.setProperty(DATASOURCE_URL, commands.get(DB));
        if (commands.containsKey(USER))
            localProperties.setProperty(DATASOURCE_USERNAME, commands.get(USER));
        if (commands.containsKey(PASSWORD))
            localProperties.setProperty(DATASOURCE_PASSWORD, commands.get(PASSWORD));
        try (OutputStream output = new FileOutputStream(APPLICATION_LOCAL_PROPERTIES)) {
            localProperties.store(output, null);
        }

    }
}
