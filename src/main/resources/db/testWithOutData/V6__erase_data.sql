-- En la nube
-- USE `b8iyr7xai8wk75ismpbt`;

-- En local
USE
`todolist-api2`;

SET
FOREIGN_KEY_CHECKS=0;

DELETE
FROM `group`;
DELETE
FROM `group_user`;
DELETE
FROM `task`;
DELETE
FROM `user`;
DELETE
FROM `user_task`;

ALTER TABLE `group` AUTO_INCREMENT = 1;
ALTER TABLE `group_user` AUTO_INCREMENT = 1;
ALTER TABLE `task` AUTO_INCREMENT = 1;
ALTER TABLE `user` AUTO_INCREMENT = 1;
ALTER TABLE `user_task` AUTO_INCREMENT = 1;

SET
FOREIGN_KEY_CHECKS=1;
