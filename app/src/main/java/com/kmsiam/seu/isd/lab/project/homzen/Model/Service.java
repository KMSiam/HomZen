package com.kmsiam.seu.isd.lab.project.homzen.Model;

public class Service {
    private String name;
    private String category;
    private String price;
    private String description;
    private String imageUrl; // Changed from int imageResId to String imageUrl

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
    public String getImageUrl() { return imageUrl; } // Updated getter
}