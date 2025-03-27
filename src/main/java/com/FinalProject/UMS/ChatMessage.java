package com.FinalProject.UMS;

public class ChatMessage {
    private final String sender;
    private final String content;
    private final boolean isError;
    private final long timestamp;

    public ChatMessage(String sender, String content, boolean isError) {
        this.sender = sender;
        this.content = content;
        this.isError = isError;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public boolean isError() {
        return isError;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", sender, content);
    }
} 