package com.shashi.luffy.Model;

public class FavouriteChatModel {
    String favourite,uid;
    public FavouriteChatModel(){
        
    }

    public FavouriteChatModel(String favourite, String uid) {
        this.favourite = favourite;
        this.uid = uid;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
