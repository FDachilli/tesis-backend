package com.tesis.hangouts;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase que se utiliza para el parseo de los datos exportados por hangouts
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Conversation {
    @JsonProperty("participant_data")
    private List<ParticipantData> participantData;
    @JsonProperty("name")
    private String name;

    public Conversation() {}

    public List<ParticipantData> getParticipantDataList() {
        return participantData;
    }

    public void setParticipantData(List<ParticipantData> participantData) {
        this.participantData = participantData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}