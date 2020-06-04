package com.appster.turtle.model;

public class AvatarModel {

    public AvatarModel(int id, String value, int imageType) {
        this.id = id;
        this.value = value;
        this.imageType = imageType;
    }

    private int id;
    private String value;
    private int imageType;

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
