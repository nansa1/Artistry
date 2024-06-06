package com.example.artistry.Model;

public class Blog {

    private String description;
    private String postid;
    private String publisher;
    private String tittle;

    public Blog() {
    }

    public Blog(String description, String postid, String publisher, String tittle) {
        this.description = description;
        this.postid = postid;
        this.publisher = publisher;
        this.tittle = tittle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }
}
