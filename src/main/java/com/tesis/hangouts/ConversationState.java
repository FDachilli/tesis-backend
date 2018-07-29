package com.tesis.hangouts;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase que se utiliza para el parseo de los datos exportados por hangouts
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationState {
    @JsonProperty("conversation_id")
    private ConversationId conversationId;
    @JsonProperty("conversation")
    private Conversation conversation;
    @JsonProperty("event")
    private List<Event> events;

    public ConversationState() {}

    public ConversationId getConversationId() {
        return conversationId;
    }

    public void setConversationId(ConversationId conversationId) {
        this.conversationId = conversationId;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}