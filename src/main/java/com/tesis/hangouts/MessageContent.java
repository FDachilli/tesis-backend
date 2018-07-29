package com.tesis.hangouts;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase que se utiliza para el parseo de los datos exportados por hangouts
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageContent {
    @JsonProperty("segment")
    private List<Segment> segments;

    public MessageContent() {}

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segment) {
        this.segments = segment;
    }
}