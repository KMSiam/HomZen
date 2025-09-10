package com.kmsiam.seu.isd.lab.project.homzen.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kmsiam.seu.isd.lab.project.homzen.Model.CartItem;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Grocery;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CartManager {
    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_ITEMS_KEY = "cart_items";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public CartManager(Context context) {
        sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addToCart(Grocery grocery) {
        ArrayList<CartItem> cartItems = getCartItems();
        boolean itemExists = false;
        
        // Check if item already exists in cart
        for (CartItem item : cartItems) {
            if (item.getGrocery().getName().equals(grocery.getName())) {
                item.setQuantity(item.getQuantity() + 1);
                itemExists = true;
                break;
            }
        }
        
        // If item doesn't exist, add it to cart
        if (!itemExists) {
            cartItems.add(new CartItem(grocery, 1));
        }
        
        saveCartItems(cartItems);
    }

    public void removeFromCart(int position) {
        ArrayList<CartItem> cartItems = getCartItems();
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            saveCartItems(cartItems);
        }
    }

    public void updateCartItemQuantity(int position, int quantity) {
        ArrayList<CartItem> cartItems = getCartItems();
        if (position >= 0 && position < cartItems.size() && quantity > 0) {
            cartItems.get(position).setQuantity(quantity);
            saveCartItems(cartItems);
        }
    }

    public ArrayList<CartItem> getCartItems() {
        String json = sharedPreferences.getString(CART_ITEMS_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        
        Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public double getCartTotal() {
        ArrayList<CartItem> cartItems = getCartItems();
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public int getCartItemCount() {
        int count = 0;
        for (CartItem item : getCartItems()) {
            count += item.getQuantity();
        }
        return count;
    }

    public void clearCart() {
        sharedPreferences.edit().remove(CART_ITEMS_KEY).apply();
    }

    public void saveCartItems(ArrayList<CartItem> cartItems) {
        String json = gson.toJson(cartItems);
        sharedPreferences.edit().putString(CART_ITEMS_KEY, json).apply();
    }
}
