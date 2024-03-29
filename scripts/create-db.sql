drop database if exists `todolist-api2`;
create database `todolist-api2`
    character set = 'utf8mb4'
    collate = 'utf8mb4_unicode_ci';

grant select, insert, update, delete, create, drop, references, index, alter,
    create temporary tables, lock tables, create view, create routine,
    alter routine, execute, trigger, show view
    on `todolist-api2`.* to 'acme-user'@'%';