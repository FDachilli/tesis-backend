package com.tesis.hangouts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase que se utiliza para el parseo de los datos exportados por hangouts
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParticipantData {
    @JsonProperty("id")
    private ParticipantId participantId;
    @JsonProperty("fallback_name")
    private String fallbackName;

    public ParticipantData() {}

    public ParticipantId getParticipantId() {
        return participantId;
    }

    public void setParticipantId(ParticipantId participantId) {
        this.participantId = participantId;
    }

    public String getFallbackName() {
        return fallbackName;
    }

    public void setFallbackName(String fallbackName) {
        this.fallbackName = fallbackName;
    }
}