package com.tesis.hangouts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase que se utiliza para el parseo de los datos exportados por hangouts
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessage {
    @JsonProperty("message_content")
    private MessageContent messageContent;

    public ChatMessage() {}

    public MessageContent getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(MessageContent messageContent) {
        this.messageContent = messageContent;
    }
}