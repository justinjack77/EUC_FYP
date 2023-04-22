package com.example.euc;

public class CombinedMessage {
    private String senderId;
    private String receiverId;
    private String text;
    private Long timestamp;

    public CombinedMessage(String senderId){
        this.senderId = senderId;
    }
    public CombinedMessage(String senderId, String receiverId, String text, Long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getText() {
        return text;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
