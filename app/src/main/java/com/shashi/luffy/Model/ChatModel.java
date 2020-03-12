package com.shashi.luffy.Model;

public class ChatModel {
    String message,sender,timestamp,receiver,type;
    boolean isseen;
    public ChatModel(){

    }

    public ChatModel(String message, String sender, String timestamp, String receiver, String type, boolean isseen) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
        this.receiver = receiver;
        this.type = type;
        this.isseen = isseen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
