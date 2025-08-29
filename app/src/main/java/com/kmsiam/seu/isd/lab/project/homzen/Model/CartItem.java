package com.kmsiam.seu.isd.lab.project.homzen.Model;

public class CartItem {
    private Grocery grocery;
    private int quantity;

    public CartItem(Grocery grocery, int quantity) {
        this.grocery = grocery;
        this.quantity = quantity;
    }

    public Grocery getGrocery() {
        return grocery;
    }

    public void setGrocery(Grocery grocery) {
        this.grocery = grocery;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        try {
            // Remove any non-numeric characters except decimal point
            String priceStr = grocery.getPrice().replaceAll("[^\\d.]", "");
            double price = Double.parseDouble(priceStr);
            return price * quantity;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
