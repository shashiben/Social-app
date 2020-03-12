package com.shashi.luffy.Model;

public class UserModel {
    private String name,email,image,onlineStatus,typingTo,gender;
    private String  uid;
    public UserModel(){
    }

    public UserModel(String name, String email, String image, String onlineStatus, String typingTo, String gender, String  uid) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
        this.gender = gender;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String  getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
