package com.kmsiam.seu.isd.lab.project.homzen.Model;

import com.kmsiam.seu.isd.lab.project.homzen.R;

public class Service {
    private String name, category, price, description;
    private int imageResId;
    private ServiceProvider provider;

    public Service(String name, String category, String price, String description, int imageResId) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageResId = imageResId;
        // Default provider
        this.provider = new ServiceProvider("Services Provider", "Professional service provider with years of experience.",
                                          "+880 **** ******", "contact@provider.com", 5.0f, 100, R.mipmap.ic_launcher);
    }

    public Service(String name, String category, String price, String description, int imageResId, 
                  ServiceProvider provider) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageResId = imageResId;
        this.provider = provider;
    }

    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getPrice() { return price; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
    public ServiceProvider getProvider() { return provider; }
    
    public static class ServiceProvider {
        private String name, description, phone, email;
        private float rating;
        private int reviewCount, imageResId;
        public ServiceProvider(String name, String description, String phone, String email, 
                             float rating, int reviewCount, int imageResId) {
            this.name = name;
            this.description = description;
            this.phone = phone;
            this.email = email;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.imageResId = imageResId;
        }

        // Getters
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public float getRating() { return rating; }
        public int getReviewCount() { return reviewCount; }
        public int getImageResId() { return imageResId; }
        
        public String getFormattedRating() {
            return String.format("⭐ %.1f (%d reviews)", rating, reviewCount);
        }
    }
}