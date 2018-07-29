package com.tesis.hangouts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Clase que se utiliza para el parseo de los datos exportados por hangouts
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationId {
    private String id;

    public ConversationId() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}