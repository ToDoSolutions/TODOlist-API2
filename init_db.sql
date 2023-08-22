-- Eliminar la base de datos si existe
DROP DATABASE IF EXISTS `todolist-api2`;

-- Crear la base de datos con un conjunto de caracteres y una collation adecuados
CREATE DATABASE `todolist-api2` CHARACTER SET = 'utf8mb4' COLLATE = 'utf8mb4_unicode_ci';

-- Crear un usuario con un nombre más seguro y una contraseña fuerte
CREATE USER 'acme_user'@'%' IDENTIFIED BY 'ACME-Us3r-P@ssw0rd';

-- Otorgar los permisos necesarios al usuario en la base de datos
GRANT ALL PRIVILEGES ON `todolist-api2`.* TO 'acme_user'@'%';

-- Asegurarse de que los permisos sean aplicados
FLUSH PRIVILEGES;
