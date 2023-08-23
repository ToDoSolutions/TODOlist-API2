package com.todolist.services;

import com.fadda.common.tuples.pair.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseService<E> {

    public void init() throws IOException {
        Pair<String, List<String>> data = loadCSVData(getEntityClass().getSimpleName());
        data.second().forEach(line -> {
            E entity = parseEntity(data.first(), line);
            saveEntity(entity);
        });
    }

    public Pair<String, List<String>> loadCSVData(String entityName) throws IOException {
        List<String> dataList = new ArrayList<>();
        String headers = null;

        // Lee el fichero
        try {
            headers = readFileAndAddToDataList(DATA + entityName + ".csv", headers, dataList);
        } catch (IOException e) {
            headers = readFileAndAddToDataList(OTHER + entityName + ".csv", headers, dataList);
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

    protected abstract void saveEntity(E entity);

    protected abstract Class<E> getEntityClass();

    private E parseEntity(String headers, String line) {
    E entity;
    try {
        entity = getEntityClass().getDeclaredConstructor().newInstance();
    } catch (Exception e) {
        throw new IllegalStateException("Error creating entity instance", e);
    }

    String[] arrayHeaders = headers.split(",");
    String[] arrayValues = line.split(",");
    for (int i = 0; i < arrayHeaders.length; i++) {
        String header = arrayHeaders[i].trim();
        String data = arrayValues[i].trim();
        Field field;
        try {
            // Try to get the field from the subclass
            field = getEntityClass().getDeclaredField(header);
        } catch (NoSuchFieldException e) {
            try {
                // Try to get the field from the superclass
                field = getEntityClass().getSuperclass().getDeclaredField(header);
            } catch (NoSuchFieldException ex) {
                throw new IllegalStateException("Invalid field: " + header, ex);
            }
        }

        field.setAccessible(true);
        setField(entity, field, data);
    }

    return entity;
}


    private void setField(E entity, Field field, String data) {
        try {
            Class<?> fieldType = field.getType();
            if (fieldType == Integer.class || fieldType == int.class) {
                field.set(entity, Integer.parseInt(data));
            } else if (fieldType == String.class) {
                field.set(entity, data);
            } else {
                field.set(entity, LocalDate.parse(data));
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Error setting field: " + field.getName(), e);
        }
    }

    // Constants ---------------------------------------------------------------
    static final String DATA = "src/main/resources/db/data/";
    static final String OTHER = "/resources/db/data/";
}

