SET FOREIGN_KEY_CHECKS = 0;

USE `todolist-api2`;

INSERT INTO `task` (`annotation`, `description`, `difficulty`, `finished_date`, `priority`, `start_date`,
                    `status`, `title`, `work_space_id`)
VALUES ('...', '...', 'HARDCORE', '2020-01-31', 5, '2020-01-01', 'DRAFT', 'TODOlist-API2', '63f4b2b1ea5a61172911b30d'),
       ('...', '...', 'HARDCORE', '2022-02-25', 5, '2022-03-17', 'DRAFT', 'Acme-L3-D02', '63e51b85d5d9cc1a6ff7f2a0');

INSERT INTO `user` (`avatar`, `bio`, `email`, `location`, `name`, `surname`, `password`, `username`, `token`, `clockify_id`)
VALUES ('...', '...', '...', '...', 'Alejandro', 'Santiago Félix', '123456', 'alesanfe' , '...', '6346cc6798d3761140bf88d2'),
       ('...', '...', '...', '...', 'María', 'Vico Martín', '123456', 'marvicmar' , '...', '636bf399d1e99e4a3364838e'),
       ('...', '...', '...', '...', 'Alejandro', 'Pérez Vázquez', '123456', 'alepervaz' , '...', '63e4e12705420c60297c5e7c'),
       ('...', '...', '...', '...', 'Sergio', 'Santiago Sánchez', '123456', 'sersansan2' , '...', '63554fb06377717db5f59884'),
       ('...', '...', '...', '...', 'Francisco', 'Rosso Ramírez', '123456', 'FranciscoRossoR' , '...', '6335c4fb82274401b556108a'),
       ('...', '...', '...', '...', 'Francisco', 'Rosso Ramírez', '123456', 'frarosram' , '...', '6335c4fb82274401b556108a');

INSERT INTO `user_task` (`id_user`, `id_task`)
VALUES (1, 1),
       (1, 2), (2, 2), (3, 2), (4, 2), (5, 2), (6,2);

SET FOREIGN_KEY_CHECKS = 1;
