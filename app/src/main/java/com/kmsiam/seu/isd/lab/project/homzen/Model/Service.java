package com.kmsiam.seu.isd.lab.project.homzen.Model;

public class Service {
    private String name, category, price, description;
    private int imageResId;

    public Service(String name, String category, String price, String description, int imageResId) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageResId = imageResId;
    }

    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getPrice() { return price; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
}