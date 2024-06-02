package com.example.amaderchuti.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ArticleModelWithDocId  implements Serializable {
    private String title;
    private String overview;
    private String docId;
    private String category;
    private String imageUrl;
    private String author;
    private String status;
    private String isEditorsChoice;
    private String isEditable;
    private String authorEmail;

    private ArrayList<ArticleSection> sectionList;
    public ArrayList<ArticleSection> getSectionList() {
        return sectionList;
    }

    public void setSectionList(ArrayList<ArticleSection> sectionList) {
        this.sectionList = sectionList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsEditorsChoice() {
        return isEditorsChoice;
    }

    public void setIsEditorsChoice(String isEditorsChoice) {
        this.isEditorsChoice = isEditorsChoice;
    }

    public String getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(String isEditable) {
        this.isEditable = isEditable;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }


}
