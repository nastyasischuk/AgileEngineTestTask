package com.agileengine.task.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Image implements Serializable {
    @Id
    private String id;
    private String author;
    private String camera;
    private String tags;
    @Column(name = "cropped_picture")
    private String croppedPicture;
    @Column(name = "full_picture")
    private String fullPicture;
    private Integer page;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCroppedPicture() {
        return croppedPicture;
    }

    public void setCroppedPicture(String croppedPicture) {
        this.croppedPicture = croppedPicture;
    }

    public String getFullPicture() {
        return fullPicture;
    }

    public void setFullPicture(String fullPicture) {
        this.fullPicture = fullPicture;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
