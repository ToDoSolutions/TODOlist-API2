package com.todolist.controllers.group;

import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;

@FlywayTest(additionalLocations = "db/testWithData", value = @DataSource(url ="jdbc:mariadb://localhost:3306/todolist-api2", username = "root", password = "iissi$root"))
class GetAllTest {

}
