package com.tesis.hangouts;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase que se utiliza para el parseo de los datos exportados por hangouts
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParticipantId {
    @JsonProperty("gaia_id")
    private String gaiaId;
    @JsonProperty("chat_id")
    private String chatId;

    public ParticipantId() {}

    public String getGaiaId() {
        return gaiaId;
    }

    public void setGaiaId(String gaiaId) {
        this.gaiaId = gaiaId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}