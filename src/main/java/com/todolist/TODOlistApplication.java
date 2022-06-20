package com.todolist;

import com.todolist.utilities.SQL;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TODOlistApplication {

    public static void main(String[] args) {
        // En la nube
        //SQL sql = new SQL("jdbc:mysql://uqiweqtspt5rb4xp:uWHt8scUWIMHRDzt7HCg@b8iyr7xai8wk75ismpbt-mysql.services.clever-cloud.com:3306/b8iyr7xai8wk75ismpbt", "uqiweqtspt5rb4xp", "uWHt8scUWIMHRDzt7HCg");

        // En local
        SQL sql = new SQL("jdbc:mariadb://localhost:3306/todolist-api2", "root", "iissi$root");

        sql.read("data/create.sql");
        sql.read("data/populate.sql");
        SpringApplication.run(TODOlistApplication.class, args);
    }

}
