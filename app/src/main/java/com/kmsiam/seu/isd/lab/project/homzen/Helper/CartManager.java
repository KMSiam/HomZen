package com.kmsiam.seu.isd.lab.project.homzen.Helper;

import android.content.Context;
import com.kmsiam.seu.isd.lab.project.homzen.Model.CartItem;
import java.util.ArrayList;

public class CartManager {
    private static CartManager instance;
    private ArrayList<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public ArrayList<CartItem> getCartItems() {
        return cartItems;
    }

    public void clearCart() {
        cartItems.clear();
    }
}
