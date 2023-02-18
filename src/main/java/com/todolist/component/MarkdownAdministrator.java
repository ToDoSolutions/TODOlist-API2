package com.todolist.component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Component;

@Component
public class MarkdownAdministrator {

    public void generarArchivoMarkdown(String stringMd, String nombreArchivo) throws IOException {
        // Obtener la ruta actual del JAR
        String rutaActual = System.getProperty("user.dir");

        // Crear el archivo Markdown en la misma ubicación que el JAR
        BufferedWriter writer = new BufferedWriter(new FileWriter(rutaActual + "/" + nombreArchivo + ".md"));
        writer.write(stringMd);
        writer.close();
    }
}
