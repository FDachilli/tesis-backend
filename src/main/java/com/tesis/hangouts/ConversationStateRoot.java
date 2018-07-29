package com.tesis.hangouts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase que se utiliza para el parseo de los datos exportados por hangouts
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationStateRoot {
    @JsonProperty("conversation_id")
    private ConversationId conversationId;
    @JsonProperty("conversation_state")
    private ConversationState conversationState;

    public ConversationStateRoot() {}

    public ConversationId getConversationId() {
        return conversationId;
    }

    public void setConversationId(ConversationId conversationId) {
        this.conversationId = conversationId;
    }

    public ConversationState getConversationState() {
        return conversationState;
    }

    public void setConversationState(ConversationState conversationState) {
        this.conversationState = conversationState;
    }
}
