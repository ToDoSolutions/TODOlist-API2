package com.todolist.component;

import com.fadda.common.tuples.pair.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataManager {


    // Components --------------------------------------------------------------
    private final Connection connection;

    // Constructor -------------------------------------------------------------
    @Autowired
    public DataManager(Environment environment) throws SQLException {
        String dbUrl = environment.getProperty("spring.datasource.url");
        String dbUser = environment.getProperty("spring.datasource.username");
        String dbPassword = environment.getProperty("spring.datasource.password");
        connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    private void saveData(Pair<String, List<String>> data, String entityName) {
        String tableName = "`" + entityName + "`"; // Encerrar el nombre de la tabla entre comillas invertidas

        String sql = "INSERT INTO " + tableName + " (" + data.first() + ") VALUES (";

        // Construir la cadena de placeholders para los valores
        String placeholders = data.first().replaceAll("\\w+", "?");

        // Agregar los placeholders a la consulta SQL
        sql += placeholders + ")";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (String line : data.second()) {
                String[] values = line.split(",");

                for (int i = 0; i < values.length; i++) {
                    statement.setString(i + 1, values[i].trim());
                }

                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadAndSaveData(String filePath, String entityName) throws IOException {
        Pair<String, List<String>> data = loadCSVData(filePath);
        saveData(data, entityName);
    }

    // Methods -----------------------------------------------------------------
    public Pair<String, List<String>> loadCSVData(String entityName) throws IOException {
        List<String> dataList = new ArrayList<>();
        String headers = null;

        // Lee el fichero
        try {
            headers = readFileAndAddToDataList(DATA + entityName, headers, dataList);
        } catch (IOException e) {
            headers = readFileAndAddToDataList(OTHER + entityName, headers, dataList);
        }

        return Pair.of(headers, dataList);
    }

    private String readFileAndAddToDataList(String filePath, String headers, List<String> dataList) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (headers == null) {
                    headers = line;
                } else {
                    dataList.add(line);
                }
            }
        }
        return headers;
    }

    // Constants ---------------------------------------------------------------
    private static final String DATA = "src/main/resources/db/data/";
    private static final String OTHER = "/resources/db/data/";
}

