package com.rabiamercan.myapplication.model;

public class Post {

    public String email;
    public String info;
    public String downloadUrl;
    public String score;

    public Post(String email, String info, String downloadUrl, String scoreString){
        this.email = email;
        this.info = info;
        this.downloadUrl = downloadUrl;
        this.score= scoreString;
    }
}
