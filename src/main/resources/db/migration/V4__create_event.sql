-- En la nube
-- USE `b8iyr7xai8wk75ismpbt`;

-- En local
USE `todolist-api2`;

CREATE EVENT task_expires ON SCHEDULE EVERY 1 DAY DO
    DELETE
    FROM task
    WHERE DATEDIFF(CURDATE(), STR_TO_DATE(finished_date, 'yyyy-mm-dd')) <= 0;
