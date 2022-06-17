package com.todolist.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQL {
    Connection connection = null;

    public SQL(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            System.out.println("ERROR:La dirección no es válida, el usuario o la clave.");
            ex.printStackTrace();
        }
    }

    public void crearBD(String archivo) {
        FileReader fileReader;
        try {
            File file = new File(archivo);
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            StringBuilder lineBD = new StringBuilder();
            while (line != null) {
                if (line.length() != 0) {
                    char firstChar = line.charAt(0);
                    if (firstChar != '/' && firstChar != '-') {
                        char lastChar = line.charAt(line.length() - 1);
                        if (lastChar == ';') {
                            PreparedStatement ps = connection.prepareStatement(lineBD + line);
                            ps.executeUpdate();
                            ps.close();
                            lineBD = new StringBuilder();
                        } else
                            lineBD.append(line);
                    }
                }
                line = bufferedReader.readLine();
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.INFO, ex.getMessage());
        }
    }
}
