package com.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TODOlistApplication {

    public static void main(String[] args) {
        // En la nube
        // SQL.start("jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", "uqiweqtspt5rb4xp", "uWHt8scUWIMHRDzt7HCg");

        // En local

        SpringApplication.run(TODOlistApplication.class, args);
    }
}
