package com.tesis.commons;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase que se encarga de manejar todos los archivos de propiedades que usa el sistema
 * @author martinmineo
 *
 */
public class PropertiesManager {
    private Properties prop;
    private InputStream input;

    /**
     * Constructor
     * @param filename Archivo que se usará para cargar las propiedades
     */
    public PropertiesManager(String filename) {
        
        prop = new Properties();
        input = null;
        try {

            input = new FileInputStream(filename);
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Devuelve una propiedad determinada
     * @param property Propiedad que se quiere obtener
     * @return String con el valor de la propiedad
     */
    public String getProperty(String property) {

        return prop.getProperty(property);
    }
}