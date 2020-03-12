package com.shashi.luffy.Model;

public class NotificationModel {
    /*String time,type,uid,pImage;*/
    private String userid, text, postid, time;
    private boolean ispost;

    public NotificationModel() {

    }

    public NotificationModel(String userid, String text, String postid, String time, boolean ispost) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.time = time;
        this.ispost = ispost;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }
}