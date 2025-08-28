package com.kmsiam.seu.isd.lab.project.homzen.Model;

public class Service {
    private String name, category, price,description, imageUrl;

    public Service(String name, String category, String price, String description, String imageUrl) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
}