package com.shashi.luffy.Model;

public class PostModel {
    String description,dp,email,name,post_image,timestamp,uid,pId,pLikes,pComments;
    public PostModel(){

    }

    public PostModel(String description, String dp, String email, String name, String post_image, String timestamp, String uid, String pId, String pLikes, String pComments) {
        this.description = description;
        this.dp = dp;
        this.email = email;
        this.name = name;
        this.post_image = post_image;
        this.timestamp = timestamp;
        this.uid = uid;
        this.pId = pId;
        this.pLikes = pLikes;
        this.pComments = pComments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }
}
