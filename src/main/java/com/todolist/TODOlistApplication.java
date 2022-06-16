package com.todolist;

import com.todolist.utilities.SQL;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TODOlistApplication {

    public static void main(String[] args) {
        SQL sql = new SQL("jdbc:mariadb://localhost:3306/todolist-api2", "root", "iissi$root");
        sql.crearBD("data/populate.sql");
        SpringApplication.run(TODOlistApplication.class, args);
    }

}
