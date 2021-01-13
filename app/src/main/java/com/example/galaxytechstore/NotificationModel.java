package com.example.galaxytechstore;

public class NotificationModel{

    private String notiID;
    private String image,body;
    private boolean readed;

    public NotificationModel(String notiID, String image, String body, boolean readed) {
        this.notiID = notiID;
        this.image = image;
        this.body = body;
        this.readed = readed;
    }

    public String getNotiID() {
        return notiID;
    }

    public void setNotiID(String notiID) {
        this.notiID = notiID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }
}
