package com.tesis.hangouts;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase que se utiliza para el parseo de los datos exportados por hangouts
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HangoutsJSON {
    @JsonProperty("conversation_state")
    private List<ConversationStateRoot> conversationStatesRoot;

    public HangoutsJSON() {}

    public List<ConversationStateRoot> getConversationStatesRoot() {
        return conversationStatesRoot;
    }

    public void setConversationStatesRoot(List<ConversationStateRoot> conversationStatesRoot) {
        this.conversationStatesRoot = conversationStatesRoot;
    }
}