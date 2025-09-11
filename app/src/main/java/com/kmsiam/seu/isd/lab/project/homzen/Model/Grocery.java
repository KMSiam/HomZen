package com.kmsiam.seu.isd.lab.project.homzen.Model;

public class Grocery{
    private int image;
    private String category, type, name, price;
    
    // No-argument constructor required for Firestore
    public Grocery() {
    }
    
    public Grocery(int image, String category, String type, String name, String price) {
        this.category = category;
        this.image = image;
        this.type = type;
        this.name = name;
        this.price = price;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}