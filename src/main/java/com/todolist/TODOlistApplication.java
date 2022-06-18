package com.todolist;

import com.todolist.utilities.SQL;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TODOlistApplication {

    public static void main(String[] args) {
        SQL sql = new SQL("jdbc:mariadb://34.175.10.202:3306/todolist", "root", "todolist$root");
        sql.read("data/populate.sql");
        SpringApplication.run(TODOlistApplication.class, args);
    }

}
